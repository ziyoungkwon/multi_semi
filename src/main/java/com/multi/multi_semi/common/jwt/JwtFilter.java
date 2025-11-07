package com.multi.multi_semi.common.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.multi_semi.common.exception.ApiExceptionDto;
import com.multi.multi_semi.common.exception.TokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    private static final String[] EXACT_PATHS = {
            "/health-check",
            "/auth/login",
            "/auth/signup",
            "/auth/refresh",
            "/auth/logout"
    };

    private static final String[] WILDCARD_PATHS = {
            "/auth/**",
            "/public/**",
            "/swagger-ui/**"
    };


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[JwtFilter] doFilterInternal START ===================================");
        String requestURI = request.getRequestURI();

        try {
            // 경로가 정확히 일치하면 필터 건너뛰기
            for (String path : EXACT_PATHS) {
                if (requestURI.equals(path)) {
                    filterChain.doFilter(request, response);
                    log.info("[JwtFilter - EXACT_PATHS] 요청 URI가 제외 경로에 해당하여 필터를 건너뜁니다.");
                    return;
                }
            }

            for (String path : WILDCARD_PATHS) {
                if (requestURI.matches(path.replace("**", "*"))) {
                    log.info("[JwtFilter - WILDCARD_PATHS] 요청 URI가 제외 경로에 해당하여 필터를 건너뜁니다.");
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            String accessToken = deletePrefix(request);
            log.info("[JwtFilter] AccessToken = {}", accessToken);
            if (StringUtils.hasText(accessToken)) {
                log.info("[JwtFilter] JWT 토큰이 존재합니다.");

                if (tokenProvider.validateToken(accessToken)) {

                    log.info("[JwtFilter] JWT 토큰이 유효합니다.");

                    Authentication authentication = tokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("[JwtFilter] SecurityContext에 Authentication 객체 설정 완료: {}", authentication);
                    log.info("[JwtFilter] SecurityContext에 Authentication 객체 설정 완료  authentication.getAuthorities(): {}", authentication.getAuthorities());

                    log.info("[JwtFilter] SecurityContextHolder 객체 확인: {}", SecurityContextHolder.getContext().getAuthentication());

                } else {
                    log.warn("[JwtFilter] JWT 토큰이 유효하지 않습니다.");
                }
            } else {
                log.info("[JwtFilter] JWT 토큰이 존재하지 않습니다.");
            }

            // 4. 필터 체인 계속 진행
            filterChain.doFilter(request, response);
            log.info("[JwtFilter] 필터 체인 완료 후 응답 처리");

        } catch (TokenException e) {
            log.error("[JwtFilter] 필터 처리 중 예외 발생: {}", e.getMessage(), e);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ApiExceptionDto errorResponse = new ApiExceptionDto(HttpStatus.UNAUTHORIZED, e.getMessage());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(convertObjectToJson(errorResponse));
            response.getWriter().flush();
        }

    }

    // 엑세스 토큰의 접두어 제거
    private String deletePrefix(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // http header에서 key가 Authorization인 Value에서 접두어인 Bearer 삭제
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            log.info("[deletePrefix] {}", bearerToken);
            return bearerToken.substring(7);
        }
        return null;
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

}




