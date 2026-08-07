package com.example.wellnesschatbackend.wellnesschat.dto;

import java.time.LocalDate;

/**
 * 영인의 daily_checks 테이블과 1:1로 맞출 예정인 DTO.
 * 지금은 mock 데이터 전용이고, 실제 API 스펙 나오면 필드명을 맞춰서 조정한다.
 *
 * @param date            기록 날짜
 * @param sleepHours      수면 시간
 * @param sleepPosture    수면 자세 (스키마 미반영 상태, 피드백 전달 완료)
 * @param pillowHeightCm  베개 높이(cm) (스키마 미반영 상태, 피드백 전달 완료)
 * @param neckPainScore   목 뻐근함 점수 (0~10)
 * @param notes           사용자 메모
 */
public record DailyCheck(
        LocalDate date,
        double sleepHours,
        SleepPosture sleepPosture,
        Integer pillowHeightCm,
        int neckPainScore,
        String notes
) {
}
