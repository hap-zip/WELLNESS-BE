package com.example.wellness.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wellnessBackendOpenApi() {
        return new OpenAPI().info(new Info()
                .title("몸기록 백엔드 API")
                .description("wellnesschat(챗봇)과 wellnessdailyexpert(데일리 체크·전문가용 요약 카드) API. "
                        + "daily-check/expert-card/health/login 엔드포인트는 /api/login으로 받은 accessToken을 "
                        + "Authorization: Bearer <token> 헤더로 실어 보내야 합니다. "
                        + "(persistent-signals는 아직 X-User-Id 임시 헤더를 씁니다.)")
                .version("v1"));
    }
}
