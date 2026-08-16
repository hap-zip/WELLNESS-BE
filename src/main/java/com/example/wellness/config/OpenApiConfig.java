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
                        + "daily-check/expert-card 엔드포인트는 X-User-Id 헤더가 필요하며, "
                        + "실제 인증(JWT) 붙기 전까지 쓰는 임시 값입니다.")
                .version("v1"));
    }
}
