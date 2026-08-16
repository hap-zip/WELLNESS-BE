package com.example.wellness.dailycheck.dto.response;

import java.util.List;

public record RecordsMonthResponse(
        int year,
        int month,
        List<DayRecordResponse> records,
        MonthStats stats
) {

    public record DayRecordResponse(Long id, String date, String condition, Integer intensity, List<String> zoneIds) {}

    public record MonthStats(int recordedDays, Double averageSleepMinutes, int discomfortDays) {}
}
