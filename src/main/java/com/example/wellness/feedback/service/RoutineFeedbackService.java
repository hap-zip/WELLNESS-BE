package com.example.wellness.feedback.service;

import com.example.wellness.dailyroutine.entity.DailyRoutineEntity;
import com.example.wellness.feedback.dto.RoutineFeedbackDTO;
import com.example.wellness.feedback.entity.RoutineFeedbackEntity;
import com.example.wellness.dailyroutine.repository.DailyRoutineRepository;
import com.example.wellness.feedback.repository.RoutineFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineFeedbackService {
    private final RoutineFeedbackRepository feedbackRepository;
    private final DailyRoutineRepository dailyRoutineRepository;

    @Transactional
    public void saveFeedback(Long userId, RoutineFeedbackDTO.FeedbackCreateRequest request) {
        DailyRoutineEntity dailyRoutine = dailyRoutineRepository.findById(request.getDailyRoutineId())
                .orElseThrow(() -> new IllegalArgumentException("루틴 기록을 찾을 수 없습니다."));
        if (!dailyRoutine.getUserId().equals(userId))
            throw new IllegalArgumentException("본인의 루틴에만 피드백을 남길 수 있습니다.");

        if (request.getFeedbackType() == RoutineFeedbackEntity.FeedbackType.IMMEDIATE) {
            if (dailyRoutine.getImmediateFeedbackId() != null)
                throw new IllegalArgumentException("이미 즉시 피드백이 작성되었습니다.");
        } else {
            if (!Boolean.TRUE.equals(dailyRoutine.getIsCompleted()))
                throw new IllegalArgumentException("완료된 루틴에만 효과 피드백을 남길 수 있습니다.");
            if (dailyRoutine.getDelayedFeedbackId() != null)
                throw new IllegalArgumentException("이미 효과 피드백이 작성되었습니다.");
        }

        String finalMemo = null;
        if (request.getFeedbackType() == RoutineFeedbackEntity.FeedbackType.IMMEDIATE)
            finalMemo = request.getMemo();
        RoutineFeedbackEntity feedback = RoutineFeedbackEntity.builder()
                .userId(userId)
                .dailyRoutineId(dailyRoutine.getId())
                .feedbackType(request.getFeedbackType())
                .effectStatus(request.getEffectStatus())
                .memo(finalMemo)
                .build();
        RoutineFeedbackEntity savedFeedback = feedbackRepository.save(feedback);
        if (request.getFeedbackType() == RoutineFeedbackEntity.FeedbackType.IMMEDIATE)
            dailyRoutine.updateImmediateFeedbackId(savedFeedback.getId());
        else
            dailyRoutine.updateDelayedFeedbackId(savedFeedback.getId());
    }

    public List<RoutineFeedbackDTO.PendingFeedbackResponse> getPendingFeedbacks(Long userId) {
        return dailyRoutineRepository.findAllByUserIdAndIsCompletedTrueAndDelayedFeedbackIdIsNull(userId).stream()
                .map(RoutineFeedbackDTO.PendingFeedbackResponse::from)
                .collect(Collectors.toList());
    }

    public List<RoutineFeedbackDTO.FeedbackSummaryResponse> getFeedbackSummary(Long userId) {
        return feedbackRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(RoutineFeedbackDTO.FeedbackSummaryResponse::from)
                .collect(Collectors.toList());
    }
}