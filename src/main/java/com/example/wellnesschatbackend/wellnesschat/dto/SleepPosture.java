package com.example.wellnesschatbackend.wellnesschat.dto;

/**
 * 영인 daily_checks 스키마에 아직 없는 필드(피드백 전달 완료, 반영 대기 중).
 * 실제 API 나오면 이 enum 값이 스키마와 일치하는지 꼭 재확인할 것.
 */
public enum SleepPosture {
    SUPINE,        // 천장 보고 똑바로
    PRONE,         // 엎드려 잠 (목 뻐근함과 상관관계 있는 패턴을 mock에 심어둠)
    LEFT_SIDE,
    RIGHT_SIDE,
    FREQUENT_CHANGE,
    UNKNOWN
}
