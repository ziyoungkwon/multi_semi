package com.multi.multi_semi.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * [신규] @Bean 정의를 SecurityConfigJwt에서 분리한 별도의 설정 클래스
 * (순환 참조 문제를 해결하기 위함)
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    // 이 클래스는 순수하게 Bean만 정의하므로, 생성자 주입이 간단합니다.
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * '기본' 리졸버 (Bean 이름: "defaultResolver")
     */
    @Bean("defaultResolver")
    public OAuth2AuthorizationRequestResolver defaultOAuth2AuthorizationRequestResolver() {
        return new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                "/oauth2/authorization"
        );
    }

    /**
     * '커스텀' 리졸버 (Bean 이름: "customResolver")
     * (내부에서 "defaultResolver"를 주입받아 사용)
     */
    @Bean("customResolver")
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(
            @Qualifier("defaultResolver") OAuth2AuthorizationRequestResolver defaultResolver) {

        // CustomAuthorizationRequestResolver는 @Component가 없어야 합니다.
        return new CustomAuthorizationRequestResolver(defaultResolver);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8090"));
        configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}
