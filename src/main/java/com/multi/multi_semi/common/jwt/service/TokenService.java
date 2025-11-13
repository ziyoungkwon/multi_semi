package com.multi.multi_semi.common.jwt.service;


import com.multi.multi_semi.common.exception.RefreshTokenException;
import com.multi.multi_semi.common.jwt.TokenProvider;
import com.multi.multi_semi.common.jwt.dao.RefreshTokenMapper;
import com.multi.multi_semi.common.jwt.dto.RefreshToken;
import com.multi.multi_semi.common.jwt.dto.TokenDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenMapper refreshTokenMapper;

    // 로그인 폼을 통한 로그인 전용 엑세스토큰, 리프레시토큰 발급 메서드
    @Transactional(noRollbackFor = RefreshTokenException.class)
    public TokenDto createTokenForLogin(Map<String, Object> loginData) {

        String memberEmail = (String) loginData.get("email");
        List<String> roles = (List<String>) loginData.get("roles");

        log.info("Map MemberEmail >>>>>>>>>>> {}", memberEmail);
        log.info("Map Roles >>>>>>>>>>> {}", roles);

        String refreshToken = handleRefreshTokenForLogin(memberEmail);
        String accessToken = tokenProvider.generateToken(memberEmail, roles, "A"); // 엑세스 토큰 생성 요청

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    /**
     * [신규] /login 헬퍼 메서드: RT가 없으면 새로 만들고, 만료되었으면 삭제하고 새로 만듭니다.
     */
    @Transactional(noRollbackFor = RefreshTokenException.class)
    public String handleRefreshTokenForLogin(String memberEmail) {

        Optional<RefreshToken> existingRefreshToken = refreshTokenMapper.findByEmail(memberEmail);

        if (existingRefreshToken.isPresent()) {
            RefreshToken refreshToken = existingRefreshToken.get();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiredDate = refreshToken.getExpiredAt();

            if (expiredDate.isBefore(now)) { // 만료된 경우
                // /login 시도이므로, 만료된 토큰은 삭제하고 새로 발급 진행
                refreshTokenMapper.deleteRefreshTokenByEmail(memberEmail);
                log.info("기존의 만료된 Refresh Token 삭제. email: {}", memberEmail);
            } else {
                // 만료되지 않은 경우, 기존 토큰 반환 (중복 로그인 허용)
                log.info("유효한 기존 Refresh Token 반환. email: {}", memberEmail);
                return refreshToken.getToken();
            }
        }

        // (토큰이 아예 없거나, 위에서 만료되어 삭제된 경우 실행됨)
        log.info("새 Refresh Token 발급. email: {}", memberEmail);
        String newRefreshToken = tokenProvider.generateToken(memberEmail, null, "R");

        if (tokenProvider.validateToken(newRefreshToken)) { // 생성된 RT가 유효한지 확인
            RefreshToken newRefreshTokenDto = RefreshToken.builder()
                    .email(memberEmail)
                    .token(newRefreshToken)
                    .expiredAt(tokenProvider.getRefreshTokenExpiry()) //
                    .createdAt(LocalDateTime.now())
                    .build();
            refreshTokenMapper.insertRefreshToken(newRefreshTokenDto); //
        }

        return newRefreshToken;
    }





    /**
     * [신규] 갱신 전용: RT를 검증하고, 새 Access Token만 발급합니다.
     * (님이 제안하신 바로 그 메서드입니다)
     *
     * @param expiredAccessToken 만료된 Access Token (사용자 정보 추출용)
     * @param clientRefreshToken 클라이언트가 보낸 Refresh Token (검증용)
     * @return New Access Token
     */
    @Transactional(noRollbackFor = RefreshTokenException.class)
    public String refreshAccessToken(String expiredAccessToken, String clientRefreshToken) {

        // 1. 클라이언트가 보낸 RT가 유효한지 먼저 검증 (만료, 위조 여부)
        if (!tokenProvider.validateToken(clientRefreshToken)) {
            // 이 검증은 DB 조회 전에 실패하므로, "만료된 RT로 두번 요청" 버그를 막아줍니다.
            // validateToken 내부에서 이미 TokenException을 던집니다.
            // (만약의 경우를 대비해 한번 더)
            throw new RefreshTokenException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. 만료된 AT에서 사용자 정보(이메일, 권한) 추출
        String jwt = deletePrefix(expiredAccessToken);
        Claims claims = tokenProvider.parseClaims(jwt); // 만료된 토큰도 parseClaims는 정보를 꺼내줌
        String memberEmail = claims.getSubject(); // 이메일

        String roleString = (String) claims.get(TokenProvider.AUTHORITIES_KEY); // 권한
        if (roleString == null) {
            throw new RefreshTokenException("Access Token에 권한 정보(auth)가 없습니다.");
        }
        List<String> roles = Arrays.asList(roleString.split(","));

        // 3. DB의 RT와 클라이언트가 보낸 RT를 비교 검증
        Optional<RefreshToken> existingRT = refreshTokenMapper.findByEmail(memberEmail);

        // 3-1. (보안) DB에 RT가 존재하지 않는가? (이미 로그아웃했거나, 비정상 접근)
        if (existingRT.isEmpty()) {
            log.warn("DB에 Refresh Token이 존재하지 않습니다. 갱신 거부. email: {}", memberEmail);
            throw new RefreshTokenException("Refresh Token이 존재하지 않습니다. 다시 로그인해 주세요.");
        }

        RefreshToken dbRefreshToken = existingRT.get();

        // 3-2. (보안) 클라이언트 RT와 DB의 RT가 일치하지 않는가? (탈취 의심)
        if (!dbRefreshToken.getToken().equals(clientRefreshToken)) {
            log.warn("클라이언트의 Refresh Token이 DB와 일치하지 않습니다. 탈취 의심. email: {}", memberEmail);
            refreshTokenMapper.deleteRefreshTokenByEmail(memberEmail); // 탈취 시도 시, DB 토큰 즉시 삭제
            throw new RefreshTokenException("Refresh Token이 일치하지 않습니다. 다시 로그인해 주세요.");
        }

        // 3-3. (시간) DB의 RT가 만료되었는가? (validateToken에서 이미 걸렀지만, DB 기준으로 재확인)
        if (dbRefreshToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            log.warn("DB의 Refresh Token이 만료되었습니다. 갱신 거부. email: {}", memberEmail);
            refreshTokenMapper.deleteRefreshTokenByEmail(memberEmail); // 만료된 토큰 삭제
            throw new RefreshTokenException("Refresh Token이 만료되었습니다. 다시 로그인해 주세요.");
        }

        // 4. 모든 검증 통과: 새 Access Token만 발급
        log.info("Refresh Token 검증 성공. 새 Access Token 발급. email: {}", memberEmail);
        return tokenProvider.generateToken(memberEmail, roles, "A");
    }


    // 받은 엑세스 토큰을 이용해 db에서 리프레시 토큰 삭제
    @Transactional
    public void deleteRefreshToken(String accessToken) {
        String token = deletePrefix(accessToken); // 받은 엑세스 토큰에서 접두어 제거
        String memberEmail = tokenProvider.getUserEmailInSubject(token); // 엑세스 토큰의 클레임의 sub에 저장한 이메일 가져오기
        refreshTokenMapper.deleteRefreshTokenByEmail(memberEmail); // 이메일로 리프레시 토큰 삭제
        log.info("refresh token 삭제 완료 : 사용자 email = {}", memberEmail);
    }


    // 엑세스 토큰에서 접두어 제거
    private String deletePrefix(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }


}
