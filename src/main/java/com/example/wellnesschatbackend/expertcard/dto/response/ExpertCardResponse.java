package com.example.wellnesschatbackend.expertcard.dto.response;

import java.util.List;

public record ExpertCardResponse(
        Long id,
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
