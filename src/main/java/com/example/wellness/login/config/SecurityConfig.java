package com.example.wellness.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * spring-security-web/config가 없어서(BCrypt용 spring-security-crypto만 의존) SecurityFilterChain은
 * 못 씀. CORS는 Spring MVC 표준 방식(WebMvcConfigurer)으로 여기서 같이 관리한다.
 * 로컬 개발 중인 프론트(포트 무관)에서 오는 요청만 허용 — 배포된 프론트 주소가 정해지면 여기 추가할 것.
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
