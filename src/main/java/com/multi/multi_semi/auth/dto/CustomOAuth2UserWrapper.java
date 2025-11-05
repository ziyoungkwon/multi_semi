package com.multi.multi_semi.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2UserWrapper implements OAuth2User {
    private final CustomUser customUser;
    private final Map<String, Object> attributes;

    public CustomOAuth2UserWrapper(CustomUser customUser, Map<String, Object> attributes) {
        this.customUser = customUser;
        this.attributes = attributes;
    }

    // --- OAuth2User 인터페이스 메서드 ---
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // CustomUser의 권한을 위임
        return this.customUser.getAuthorities();
    }

    @Override
    public String getName() {
        // CustomUser의 getUsername() (이메일)을 고유 식별자로 사용
        return this.customUser.getUsername();
    }

    // --- 헬퍼 메서드 (SuccessHandler에서 사용) ---
    public CustomUser getCustomUser() {
        return this.customUser;
    }
}
