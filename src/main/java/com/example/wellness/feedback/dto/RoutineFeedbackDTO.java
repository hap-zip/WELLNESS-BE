package com.example.wellness.feedback.dto;

import com.example.wellness.dailyroutine.entity.DailyRoutineEntity;
import com.example.wellness.feedback.entity.RoutineFeedbackEntity;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RoutineFeedbackDTO {
    @Getter
    public static class FeedbackCreateRequest {
        private Long dailyRoutineId;
        private RoutineFeedbackEntity.FeedbackType feedbackType;
        private RoutineFeedbackEntity.EffectStatus effectStatus;
        private String memo;
    }

    @Getter
    @Builder
    public static class PendingFeedbackResponse {
        private Long dailyRoutineId;
        private Long routineId;
        private String targetArea;
        private LocalDate targetDate;
        public static PendingFeedbackResponse from(DailyRoutineEntity dr) {
            return PendingFeedbackResponse.builder()
                    .dailyRoutineId(dr.getId())
                    .routineId(dr.getRoutine().getId())
                    .targetArea(dr.getRoutine().getTargetArea())
                    .targetDate(dr.getTargetDate())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FeedbackSummaryResponse {
        private Long feedbackId;
        private Long dailyRoutineId;
        private String feedbackType;
        private String effectStatus;
        private LocalDateTime createdAt;
        public static FeedbackSummaryResponse from(RoutineFeedbackEntity feedback) {
            return FeedbackSummaryResponse.builder()
                    .feedbackId(feedback.getId())
                    .dailyRoutineId(feedback.getDailyRoutineId())
                    .feedbackType(feedback.getFeedbackType().name())
                    .effectStatus(feedback.getEffectStatus().name())
                    .createdAt(feedback.getCreatedAt())
                    .build();
        }
    }
}