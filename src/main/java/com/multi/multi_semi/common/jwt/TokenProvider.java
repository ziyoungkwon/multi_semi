package com.multi.multi_semi.common.jwt;


import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.common.exception.TokenException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {

    public static final String AUTHORITIES_KEY = "auth";  // 클레임에서 권한정보담을키
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 10;     //1분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60; //1000L * 60 * 60 * 24 * 1;  // 1일

    private final ByteSecretKeyProvider byteSecretKeyProvider;  // JwtProvider 의존성 추가
    private final Key SKEY; // 디코딩된 문자열 비밀키
    private final String ISSUER; // 발급자(jwt를 발급한 서버를 의미)

    //application.yml 에 정의해놓은 jwt.secret 값을 가져와서 JWT 를 만들 때 사용하는 암호화 키값을 생성
    public TokenProvider(ByteSecretKeyProvider byteSecretKeyProvider) {
        this.byteSecretKeyProvider = byteSecretKeyProvider;
        this.SKEY = byteSecretKeyProvider.getSecretKey(); // 디코딩된 문자열 비밀키
        this.ISSUER = byteSecretKeyProvider.getIssuer(); // 발급자(서버를 의미)
        System.out.println("TokenProvider-------------" + SKEY);
        System.out.println("   ISSUER     -------------" + ISSUER);
    }

    public String generateToken(String memberEmail, List<String> roles, String code){

        // 페이로드에 넣을 클레임 생성 및 이메일 삽입
        // 역할은 엑세스 토큰에만 넣고, 리프레시 토큰엔 안넣음
        Claims claims = Jwts
                .claims()
                .setSubject(memberEmail); // 클레임의 sub에 email저장


        Date tokenExpirationTime = new Date(); // 토큰의 만료시간
        long now = new Date().getTime(); // 현재 시간
        if(code.equals("A")){
            tokenExpirationTime = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
            claims.put(AUTHORITIES_KEY, String.join(",", roles)); // 엑세스 토큰에 역할 넣기
        }
        else if(code.equals("R")){
            tokenExpirationTime = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        }

        // 토큰 실제로 생성해서 문자열 형태로 반환
        return Jwts.builder()
                .setIssuer(ISSUER) // 발급자 설정
                .setIssuedAt(new Date(now)) // 발급 시각 설정
                .setClaims(claims) // 페이로드에 넣을 claim설정
                .setExpiration(tokenExpirationTime) // 만료 시각 설정
                .signWith(SKEY, SignatureAlgorithm.HS512) // 지정한 키와 알고리즘으로 시그니처 생성
                .compact(); // jwt를 문자열 형태로 인코딩하여 반환
    }

    public boolean validateToken(String refreshToken) {

        try{
            log.info("[TokenProvider] 유효성 검증 중인 토큰: {}", refreshToken);

            // 토큰을 비밀키와 함께 복호화해서 유효하지 않으면 false, 유효하면 true반환
            Jwts.parserBuilder()
                    .setSigningKey(SKEY)
                    .build()
                    .parseClaimsJws(refreshToken);

            log.info("[TokenProvider] JWT 토큰이 유효합니다");
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("[TokenProvider] 잘못된 JWT 서명입니다. 토큰: {}", refreshToken, e);
            throw new TokenException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("[TokenProvider] 만료된 JWT 토큰입니다. 토큰: {}, 만료 시각: {}", refreshToken, e.getClaims().getExpiration(), e);
            throw new TokenException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("[TokenProvider] 지원되지 않는 JWT 토큰입니다. 토큰: {}", refreshToken, e);
            throw new TokenException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("[TokenProvider] JWT 토큰이 잘못되었습니다. 토큰: {}", refreshToken, e);
            throw new TokenException("JWT 토큰이 잘못되었습니다.");
        }
    }



    // Authentication 반환 메소드
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken); // 엑세스 토큰에서 클레인 가져오기

        if(claims.get(AUTHORITIES_KEY) == null){ // 권한이 없으면 예외발생
            throw new RuntimeException("권한정보가 없는 토큰입니다");
        }

        // 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        log.info("[TokenProvider] authorities: {}", authorities); // 권한 로그

        CustomUser customUser = new CustomUser();
        customUser.setEmail(claims.getSubject()); // 클레임에서 이메일 추출해서 set
        customUser.setAuthorities(authorities); // 추출한 권한 set

        return new UsernamePasswordAuthenticationToken(customUser, "", authorities);
    }

    // 엑세스 토큰의 페이로드에 있는 클레임을 반환
    public Claims parseClaims(String accessToken) {
        try{
            return Jwts
                    .parserBuilder()
                    .setSigningKey(SKEY)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        }catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }

    // 엑세스 토큰의 페이로드에 있는 클레임에 있는 sub에 있는 email 반환
    public String getUserEmailInSubject(String accessToken){
        return Jwts
                .parserBuilder() // jwt 파서를 만들기 위한 빌더 생성
                .setSigningKey(SKEY) // 검증에 사용할 키 설정(토큰 위조 검증)
                .build() // 실제 파서 객체 생성
                .parseClaimsJws(accessToken) // 엑세스 토큰(문자열)을 파싱 및 검증
                .getBody() // 페이로드 추출
                .getSubject(); // "sub" 클레임 값을 반환
    }


    public LocalDateTime getRefreshTokenExpiry() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reTokenExpiry = now.plus(REFRESH_TOKEN_EXPIRE_TIME, ChronoUnit.MILLIS);
        return reTokenExpiry;
    }

    // [신규 추가] RT 만료 시간을 '초(second)' 단위로 반환하는 public 메서드
    public long getRefreshTokenExpirySeconds() {
        return REFRESH_TOKEN_EXPIRE_TIME / 1000;
    }
}

