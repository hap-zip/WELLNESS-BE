package com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.dto.response;

import java.util.List;
import java.util.UUID;

public record ExpertCardResponse(
        UUID id,
        String period,
        String startDate,
        String endDate,
        String headline,
        List<HighlightResponse> highlights,
        List<String> discomfortAreas,
        List<String> sleepPostures,
        int routineCount,
        String feedbackSummary,
        List<String> discoveredPatterns,
        String note,
        String createdAt
) {

    public record HighlightResponse(String label, String value, String change) {}
}
