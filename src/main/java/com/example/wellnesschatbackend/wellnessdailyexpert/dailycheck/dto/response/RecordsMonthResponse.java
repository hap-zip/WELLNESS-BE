package com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.dto.response;

import java.util.List;
import java.util.UUID;

public record RecordsMonthResponse(
        int year,
        int month,
        List<DayRecordResponse> records,
        MonthStats stats
) {

    public record DayRecordResponse(UUID id, String date, String condition, Integer intensity, List<String> zoneIds) {}

    public record MonthStats(int recordedDays, Double averageSleepMinutes, int discomfortDays) {}
}
