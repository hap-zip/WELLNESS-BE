package com.example.wellness.connectionview.service;

import com.example.wellness.connectionview.dto.PainConnectionResponse;
import com.example.wellness.dailycheck.entity.DailyCheck;
import com.example.wellness.dailycheck.repository.DailyCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConnectionService {
    private final DailyCheckRepository dailyCheckRepository;

    public List<PainConnectionResponse> getConnections(Long userId, LocalDate startDate, LocalDate endDate) {
        return dailyCheckRepository.findAllByUserIdAndCheckDateBetween(userId, startDate, endDate).stream()
                .map(PainConnectionResponse::from)
                .collect(Collectors.toList());
    }

    public PainConnectionResponse getDailySummary(Long userId, LocalDate date) {
        DailyCheck dailyCheck = dailyCheckRepository.findByUserIdAndCheckDate(userId, date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 기록이 없습니다."));
        return PainConnectionResponse.from(dailyCheck);
    }
}