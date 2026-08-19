package com.example.wellness.dailyroutine.service;

import com.example.wellness.dailyroutine.dto.RoutineDTO.*;
import com.example.wellness.dailyroutine.entity.DailyRoutineEntity;
import com.example.wellness.dailyroutine.entity.RoutineEntity;
import com.example.wellness.dailyroutine.repository.DailyRoutineRepository;
import com.example.wellness.dailyroutine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final DailyRoutineRepository dailyRoutineRepository;

    public TodayRoutineResponse getTodayRoutine(Long userId) {
        DailyRoutineEntity dailyRoutine = dailyRoutineRepository.findByUserIdAndTargetDate(userId, LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("오늘 추천된 루틴이 없습니다."));
        return TodayRoutineResponse.from(dailyRoutine);
    }

    public RoutineDetailResponse getRoutineDetail(Long routineId) {
        RoutineEntity routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 루틴입니다."));
        return RoutineDetailResponse.from(routine);
    }

    @Transactional
    public void completeRoutine(Long dailyRoutineId, Long userId) {
        DailyRoutineEntity dailyRoutine = dailyRoutineRepository.findById(dailyRoutineId)
                .orElseThrow(() -> new IllegalArgumentException("매칭된 루틴 기록이 없습니다."));
        if (!dailyRoutine.getUserId().equals(userId))
            throw new IllegalArgumentException("권한이 없습니다.");
        dailyRoutine.markAsCompleted();
    }

    public List<CompletionResponse> getCompletionsByPeriod(Long userId, LocalDate startDate, LocalDate endDate) {
        return dailyRoutineRepository.findAllByUserIdAndIsCompletedTrueAndTargetDateBetween(userId, startDate, endDate)
                .stream()
                .map(CompletionResponse::from)
                .collect(Collectors.toList());
    }

    public CompletionResponse getCompletionDetail(Long completionId) {
        DailyRoutineEntity dailyRoutine = dailyRoutineRepository.findById(completionId)
                .orElseThrow(() -> new IllegalArgumentException("완료 기록이 없습니다."));
        return CompletionResponse.from(dailyRoutine);
    }
}