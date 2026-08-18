package com.example.wellness.connectionview.dto;

import com.example.wellness.dailycheck.entity.DailyCheck;
import com.example.wellness.dailycheck.entity.DailyCheckPainArea;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PainConnectionResponse {
    private Long id;
    private Long userId;
    private LocalDate checkDate;
    private String condition;
    private Integer sleepSatisfaction;
    private List<String> skinStates;
    private Integer autoSleepDurationMinutes;
    private Integer autoSteps;
    private List<PainAreaDTO> painAreas;

    @Getter
    @Builder
    public static class PainAreaDTO {
        private String zoneId;
        private Integer intensity;
    }
    public static PainConnectionResponse from(DailyCheck dc) {
        List<PainAreaDTO> painAreaList = null;
        if (dc.getPainAreas() != null && !dc.getPainAreas().isEmpty()) {
            painAreaList = dc.getPainAreas().stream()
                    .map((DailyCheckPainArea area) -> PainAreaDTO.builder()
                            .zoneId(area.getZoneId())
                            .intensity(area.getIntensity())
                            .build())
                    .collect(Collectors.toList());
        }
        return PainConnectionResponse.builder()
                .id(dc.getId())
                .userId(dc.getUserId())
                .checkDate(dc.getCheckDate())
                .condition(dc.getCondition() != null ? dc.getCondition().name() : null)
                .sleepSatisfaction(dc.getSleepSatisfaction())
                .skinStates(dc.getSkinStates())
                .autoSleepDurationMinutes(dc.getAutoSleepDurationMinutes())
                .autoSteps(dc.getAutoSteps())
                .painAreas(painAreaList)
                .build();
    }
}