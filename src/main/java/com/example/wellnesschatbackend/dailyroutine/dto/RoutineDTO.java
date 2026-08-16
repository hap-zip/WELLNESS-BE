package com.example.wellnesschatbackend.dailyroutine.dto;

import com.example.wellnesschatbackend.dailyroutine.entity.DailyRoutineEntity;
import com.example.wellnesschatbackend.dailyroutine.entity.RoutineEntity;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RoutineDTO {
    @Getter
    @Builder
    public static class TodayRoutineResponse {
        private Long dailyRoutineId;
        private Long routineId;
        private String targetArea;
        private Integer totalDurationMinutes;
        private Boolean isCompleted;
        public static TodayRoutineResponse from(DailyRoutineEntity dr) {
            return TodayRoutineResponse.builder()
                    .dailyRoutineId(dr.getId())
                    .routineId(dr.getRoutine().getId())
                    .targetArea(dr.getRoutine().getTargetArea())
                    .totalDurationMinutes(dr.getRoutine().getTotalDurationMinutes())
                    .isCompleted(dr.getIsCompleted())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RoutineDetailResponse {
        private Long routineId;
        private String targetArea;
        private String description;
        private Integer totalDurationMinutes;
        private String precautions;
        private List<Map<String, Object>> stepsData;
        public static RoutineDetailResponse from(RoutineEntity routine) {
            return RoutineDetailResponse.builder()
                    .routineId(routine.getId())
                    .targetArea(routine.getTargetArea())
                    .description(routine.getDescription())
                    .totalDurationMinutes(routine.getTotalDurationMinutes())
                    .precautions(routine.getPrecautions())
                    .stepsData(routine.getStepsData())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CompletionResponse {
        private Long completionId;
        private Long routineId;
        private String targetArea;
        private LocalDate targetDate;
        private Long immediateFeedbackId;
        private Long delayedFeedbackId;
        public static CompletionResponse from(DailyRoutineEntity dr) {
            return CompletionResponse.builder()
                    .completionId(dr.getId())
                    .routineId(dr.getRoutine().getId())
                    .targetArea(dr.getRoutine().getTargetArea())
                    .targetDate(dr.getTargetDate())
                    .immediateFeedbackId(dr.getImmediateFeedbackId())
                    .delayedFeedbackId(dr.getDelayedFeedbackId())
                    .build();
        }
    }
}