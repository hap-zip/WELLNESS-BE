package com.example.wellnesschatbackend.wellnesschat.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 프론트(챗 화면)에서 넘어오는 요청.
 */
public record ChatRequest(
        @NotBlank String userId,
        @NotBlank String message
) {
}
