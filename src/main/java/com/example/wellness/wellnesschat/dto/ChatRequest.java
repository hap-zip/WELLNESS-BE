package com.example.wellness.wellnesschat.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 프론트(챗 화면)에서 넘어오는 요청.
 * userId는 더 이상 요청 body로 안 받는다 — 로그인 토큰(Authorization: Bearer)에서 @CurrentUserId로 꺼낸다.
 */
public record ChatRequest(
        @NotBlank String message
) {
}