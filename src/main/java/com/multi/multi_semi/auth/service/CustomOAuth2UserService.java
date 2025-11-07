package com.multi.multi_semi.auth.service;


import com.multi.multi_semi.auth.dto.CustomOAuth2UserWrapper;
import com.multi.multi_semi.auth.dto.CustomUser;
import com.multi.multi_semi.member.dao.MemberMapper;
import com.multi.multi_semi.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. Google로부터 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        log.info("Google 로그인 시도: email={}, name={}", email, name);

        // 2. 님의 CustomUserDetailService와 동일하게 DB에서 이메일로 회원 조회
        Optional<MemberDto> existingMemberOpt = memberMapper.findByEmail(email);

        MemberDto memberDto;

        if (existingMemberOpt.isEmpty()) {
            // 3-1. [신규 회원] DB에 없음 -> 자동 회원가입
            log.info("신규 Google 사용자. 자동 회원가입을 진행합니다.");

            // 님의 AuthService가 MemberReqDto를 사용하므로,
            // 호환성을 위해 Dto를 생성합니다.
            MemberDto newMember = MemberDto.builder()
                    .memberEmail(email)
                    .memberName(name)
                    // OAuth2 사용자는 비밀번호 로그인을 사용하지 않으므로, 랜덤 값 설정
                    .memberPassword(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .memberRole("ROLE_USER") // 기본 권한
                    .memberId(email) // memberId가 필요하다면 이메일로 임시 설정
                    .build();

            // (주의) 님의 MemberMapper에 MemberDto를 직접 insert하는 쿼리가 필요할 수 있습니다.
            // (AuthService의 signup은 MemberReqDto를 받기 때문)
            // 임시로 MemberDto를 insert하는 쿼리를 호출한다고 가정합니다.
            memberMapper.insertOAuthMember(newMember); // (이 쿼리를 MemberMapper.xml에 추가 필요)

            memberDto = newMember;

        } else {
            // 3-2. [기존 회원] DB에 있음
            log.info("기존 사용자. Google 정보로 로그인합니다.");
            memberDto = existingMemberOpt.get();
        }

        // 4. 님의 CustomUserDetailService가 빌드하는 것과
        //    동일한 CustomUser 객체를 생성합니다.
        CustomUser customUser = CustomUser.builder()
                .email(memberDto.getMemberEmail())
                .memberPassword(memberDto.getMemberPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(memberDto.getMemberRole())))
                .memberId(memberDto.getMemberId()) //
                .build();

        // 5. CustomUser(UserDetails)와 OAuth2User 정보를 래핑하여 반환
        return new CustomOAuth2UserWrapper(customUser, attributes);
    }

    /*
    --- MemberMapper.xml에 이 쿼리 추가가 필요할 수 있습니다 ---
    <insert id="insertOAuthMember" parameterType="com.multi.restproduct.member.dto.MemberDto">
        INSERT INTO member (member_id, member_email, member_name, member_password, member_role)
        VALUES (#{memberId}, #{memberEmail}, #{memberName}, #{memberPassword}, #{memberRole})
    </insert>
    */
}
