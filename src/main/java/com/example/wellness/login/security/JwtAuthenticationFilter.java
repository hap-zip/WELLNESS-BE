package com.example.wellness.login.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authorization: Bearer 토큰이 있으면 검증해서 사용자 id를 요청 attribute에 심어둔다.
 * 토큰이 없거나 잘못돼도 여기서 막지 않는다 — 실제 로그인 필요 여부 판단은
 * @CurrentUserId를 쓰는 컨트롤러 파라미터(CurrentUserIdArgumentResolver)가 담당한다.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String USER_ID_ATTRIBUTE = "currentUserId";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Long userId = jwtService.extractUserId(token);
                request.setAttribute(USER_ID_ATTRIBUTE, userId);
            } catch (JwtException | IllegalArgumentException ignored) {
                // 토큰이 만료·위조 등으로 무효하면 그냥 비로그인 상태로 흘려보낸다.
            }
        }
        filterChain.doFilter(request, response);
    }
}
