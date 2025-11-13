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

        // 2. DB에서 이메일로 회원 조회
        Optional<MemberDto> findMember = memberMapper.findMemberByEmail(email);

        MemberDto memberDto;

        if (findMember.isEmpty()) {
            // 3-1. [신규 회원] DB에 없음 -> 자동 회원가입
            log.info("신규 Google 사용자. 자동 회원가입을 진행합니다.");

            MemberDto newMember = MemberDto.builder()
                    .id(email)
                    .email(email)
                    .name(name)
                    .role("ROLE_USER") // 기본 권한
                    // OAuth2 사용자는 비밀번호 로그인을 사용하지 않으므로, 랜덤 값 설정
                    .uuid(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();

            memberMapper.insertOAuthMember(newMember);

            memberDto = newMember;

        } else {
            MemberDto existingMember = findMember.get();

            // 4. 같은 구글 이메일로 이미 직접 입력하여 회원가입은 했지만, 구글계정과 연동이 안되어 uuid는 없고 pwd만 있는 경우
            if(findMember.get().getUuid() == null){ // 여기서 isBlank()를 해야하나? isEmpty()를 해야하나?
                // 5. mem테이블의 uuid에 랜덤한 uuid값 추가
                log.info("기존 Email 사용자. Google 계정 연동을 진행합니다.");

                String uuid = passwordEncoder.encode(UUID.randomUUID().toString());
                memberMapper.updateUuidByNo(findMember.get().getNo(), uuid);
                existingMember.setUuid(uuid);

                memberDto = existingMember;
            }else { // pwd도 있고 uuid도 있는 경우
                // 3-2. [기존 회원] DB에 있음
                log.info("기존 사용자. Google 정보로 로그인합니다.");
                memberDto = existingMember;
            }
        }

        CustomUser customUser = CustomUser.builder()
                .no(memberDto.getNo())
                .email(memberDto.getEmail())
                .pwd(memberDto.getPwd()) // 구글회원가입만 했으면 null
                .uuid(memberDto.getUuid()) //
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(memberDto.getRole())))
                .id(memberDto.getId())
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
