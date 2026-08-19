package com.example.wellness.wellnesschat.dto;

import java.time.LocalDate;

/**
 * 영인의 daily_checks 테이블과 매핑되는 DTO.
 * PR #3, #4 반영: pillow는 자유텍스트(String)로, sleepHours는 autoRecords.sleepDurationMinutes(분)에서 환산.
 *
 * @param date            기록 날짜
 * @param sleepHours      수면 시간 (autoRecords.sleepDurationMinutes / 60.0)
 * @param sleepPosture    수면 자세
 * @param pillowDescription 베개 관련 메모 (실제 스키마가 자유텍스트라 숫자 cm 아님)
 * @param neckPainScore   목 뻐근함 점수 (0~10, painAreas 중 목 부위 intensity)
 * @param notes           사용자 메모
 */
public record DailyCheck(
        LocalDate date,
        double sleepHours,
        SleepPosture sleepPosture,
        String pillowDescription,
        int neckPainScore,
        String notes
) {
}