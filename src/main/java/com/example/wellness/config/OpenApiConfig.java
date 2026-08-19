package com.example.wellness.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI wellnessBackendOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("몸기록 백엔드 API")
                        .description("wellnesschat(챗봇)과 wellnessdailyexpert(데일리 체크·전문가용 요약 카드) API. "
                                + "로그인/회원가입/탈퇴를 제외한 모든 엔드포인트는 /api/login으로 받은 accessToken을 "
                                + "Authorization: Bearer <token> 헤더로 실어 보내야 합니다. "
                                + "우측 상단 Authorize 버튼에 토큰을 넣으면 Try it out 호출에 자동으로 실립니다. "
                                + "화면에 보이는 userId 파라미터는 무시해도 됩니다 — 실제로는 토큰에서 자동으로 읽어옵니다.")
                        .version("v1"))
                .servers(List.of(new Server().url("https://haeum.gamjabox.cloud").description("배포 서버")))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME_NAME,
                        new SecurityScheme()
                                .name(BEARER_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME));
    }
}
