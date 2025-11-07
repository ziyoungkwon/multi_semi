package com.multi.multi_semi.common.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.Map;


/**
 * OAuth2 요청을 커스텀하여 'prompt=consent' 파라미터를 추가합니다.
 * (항상 계정 선택 창이 뜨도록 강제)
 */
@RequiredArgsConstructor
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    // [ ★★★ 추가 ★★★ ]
    // SecurityConfigJwt에서 설정한 baseUri와 동일해야 합니다.
    private static final String AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    /**
     * [ ★★★ 수정 ★★★ ]
     * 1. 'defaultResolver'로 기본 요청을 생성합니다.
     * 2. 'getClientId()' 대신, URI에서 'registrationId'("google")를 직접 추출하는
     * 헬퍼 메서드 'resolveRegistrationId()'를 호출합니다.
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = this.defaultResolver.resolve(request);
        if (authorizationRequest == null) {
            return null;
        }

        // [ ★★★ 수정된 핵심 ★★★ ]
        // URI에서 "google" 값을 직접 추출합니다.
        String registrationId = this.resolveRegistrationId(request);

        return addPromptParameter(authorizationRequest, registrationId);
    }

    /**
     * [ ★★★ 수정 없음 (기존과 동일) ★★★ ]
     * 이 메서드는 'clientRegistrationId'를 파라미터로 올바르게 받고 있습니다.
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = this.defaultResolver.resolve(request, clientRegistrationId);
        if (authorizationRequest == null) {
            return null;
        }

        return addPromptParameter(authorizationRequest, clientRegistrationId);
    }

    /**
     * [ ★★★ 수정 없음 (기존과 동일) ★★★ ]
     * 'registrationId'가 "google"일 경우 'prompt=consent'를 추가합니다.
     */
    private OAuth2AuthorizationRequest addPromptParameter(OAuth2AuthorizationRequest authorizationRequest, String registrationId) {

        // (null 체크는 상위 메서드에서 수행함)

        if (registrationId != null && registrationId.equals("google")) {
            Map<String, Object> additionalParameters = new HashMap<>(authorizationRequest.getAdditionalParameters());

            // [핵심] "prompt=consent"를 추가
            additionalParameters.put("prompt", "consent");

            return OAuth2AuthorizationRequest.from(authorizationRequest)
                    .additionalParameters(additionalParameters)
                    .build();
        }

        // Google이 아니면(예: Naver, Kakao) 기본 요청을 그대로 반환
        return authorizationRequest;
    }

    /**
     * [ ★★★ 신규 헬퍼 메서드 ★★★ ]
     * DefaultResolver와 유사하게 URI에서 {registrationId}를 추출합니다.
     * (예: /oauth2/authorization/google -> "google" 추출)
     */
    private String resolveRegistrationId(HttpServletRequest request) {
        if (this.pathMatcher.match(AUTHORIZATION_REQUEST_BASE_URI + "/{registrationId}", request.getRequestURI())) {
            Map<String, String> variables = this.pathMatcher
                    .extractUriTemplateVariables(AUTHORIZATION_REQUEST_BASE_URI + "/{registrationId}", request.getRequestURI());
            return variables.get("registrationId");
        }
        return null;
    }
}
