package com.multi.multi_semi.common.config;


import com.multi.multi_semi.auth.service.CustomOAuth2UserService;
import com.multi.multi_semi.auth.service.OAuth2AuthenticationSuccessHandler;
import com.multi.multi_semi.common.jwt.JwtAccessDeniedHandler;
import com.multi.multi_semi.common.jwt.JwtAuthenticationEntryPoint;
import com.multi.multi_semi.common.jwt.JwtFilter;
import com.multi.multi_semi.common.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
// @RequiredArgsConstructor // (삭제됨)
public class SecurityConfigJwt {

    private final TokenProvider tokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthorizationRequestResolver customResolver;
    private final CorsConfigurationSource corsConfigurationSource;


    // 수동 생성자 (Qualifier로 순환 참조 회피)
    public SecurityConfigJwt(
            TokenProvider tokenProvider,
            JwtAccessDeniedHandler jwtAccessDeniedHandler,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            CustomOAuth2UserService customOAuth2UserService,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
            @Qualifier("customResolver") OAuth2AuthorizationRequestResolver customResolver,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource
    ) {

        this.tokenProvider = tokenProvider;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.customResolver = customResolver;
        this.corsConfigurationSource = corsConfigurationSource;
    }


    @Bean
    public SecurityFilterChain SecurityConfigJwt(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(this.corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // (기존 permitAll 경로들...)
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/img/**",     // 여기!
                                "/images/**",
                                "/webjars/**",
                                "/json/**"
                        ).permitAll()
                        .requestMatchers("/refresh/test").permitAll()
                        .requestMatchers("/api/v1/favorites/**","/favorites/**","/api/v1/favorite/**").permitAll()
                        .requestMatchers("/api/v1/places/**","/places/**").permitAll()
                        .requestMatchers("/", "/error", "/auth/**", "/login/oauth2/**", "/oauth2/**", "/oauth-redirect").permitAll()
                        .requestMatchers("/api/v1/products/**", "/api/v1/product/**", "/products/**", "/product/**").permitAll()
                        .requestMatchers("/api/v1/reviews/**", "/css/**", "/productimgs/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/v1/products-management/**", "/api/v1/reviews-management/**").hasAnyRole("ADMIN")
                        .requestMatchers("/api/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())

                // [ ★★★ 수정 ★★★ ]
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(authEndpoint ->
                                // .baseUri(...) 설정을 다시 추가합니다.
                                // 이 URI로 오는 요청에 대해
                                authEndpoint.baseUri("/oauth2/authorization")
                                        // 'customResolver'를 사용하도록 지정합니다.
                                        .authorizationRequestResolver(this.customResolver)
                        )
                        .userInfoEndpoint(ep -> ep.userService(this.customOAuth2UserService))
                        .successHandler(this.oAuth2AuthenticationSuccessHandler)
                        .failureUrl("/auth/login?error")
                )

                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );

        return http.build();
    }
}