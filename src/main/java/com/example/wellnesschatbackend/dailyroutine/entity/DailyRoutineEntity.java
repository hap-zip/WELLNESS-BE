package com.example.wellnesschatbackend.dailyroutine.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "daily_routines")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyRoutineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private RoutineEntity routine;

    @Column(name = "immediate_feedback_id")
    private Long immediateFeedbackId;

    @Column(name = "delayed_feedback_id")
    private Long delayedFeedbackId;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Builder
    public DailyRoutineEntity(Long userId, RoutineEntity routine, LocalDate targetDate) {
        this.userId = userId;
        this.routine = routine;
        this.targetDate = targetDate;
        this.isCompleted = false;
    }

    public void markAsCompleted() {
        this.isCompleted = true;
    }

    public void updateImmediateFeedbackId(Long feedbackId) {
        this.immediateFeedbackId = feedbackId;
    }

    public void updateDelayedFeedbackId(Long feedbackId) {
        this.delayedFeedbackId = feedbackId;
    }
}