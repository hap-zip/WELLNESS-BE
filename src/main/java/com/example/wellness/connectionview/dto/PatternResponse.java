package com.example.wellness.connectionview.dto;

import com.example.wellness.connectionview.entity.PatternEntity;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class PatternResponse {
    private Long id;
    private String patternName;
    private String patternType;
    private String sourceMetric;
    private String targetMetric;
    private String relationDirection;
    private String status;
    private LocalDate analysisStartDate;
    private LocalDate analysisEndDate;
    public static PatternResponse from(PatternEntity pattern) {
        return PatternResponse.builder()
                .id(pattern.getId())
                .patternName(pattern.getPatternName())
                .patternType(pattern.getPatternType().name())
                .sourceMetric(pattern.getSourceMetric())
                .targetMetric(pattern.getTargetMetric())
                .relationDirection(pattern.getRelationDirection().name())
                .status(pattern.getStatus().name())
                .analysisStartDate(pattern.getAnalysisStartDate())
                .analysisEndDate(pattern.getAnalysisEndDate())
                .build();
    }
}