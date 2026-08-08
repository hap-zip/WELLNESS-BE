package com.example.wellnesschatbackend.wellnesschat.dto;

import java.util.List;

/**
 * 챗 응답. guardrailPassed=false면 LLM 원본 대신 안전한 대체 문구(reply)가 나간다.
 * violations에는 어떤 규칙에 걸렸는지 로그/디버깅용으로 남긴다(프론트에는 노출 안 해도 됨).
 */
public record ChatResponse(
        String reply,
        boolean guardrailPassed,
        List<String> violations
) {
}
