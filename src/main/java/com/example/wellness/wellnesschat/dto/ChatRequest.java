package com.example.wellness.wellnesschat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 프론트(챗 화면)에서 넘어오는 요청.
 */
public record ChatRequest(
        @NotNull Long userId,
        @NotBlank String message
) {
}