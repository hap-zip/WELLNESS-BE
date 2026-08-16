package com.example.wellness.wellnesschat.scheduler;

import com.example.wellness.dailycheck.repository.DailyCheckRepository;
import com.example.wellness.wellnesschat.service.PersistentSignalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 매일 정해진 시각에 전체 유저를 순회하며 지속신호(persistent_signals)를 판정하는 스케줄러.
 * "지속"/"악화" 유형만 검사. "무개선"은 서진 RoutineFeedback 연동 후 추가 예정.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PersistentSignalScheduler {

    private final DailyCheckRepository dailyCheckRepository;
    private final PersistentSignalService persistentSignalService;

    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시
    public void checkAllUsersSignals() {
        List<Long> userIds = dailyCheckRepository.findAllDistinctUserIds();
        log.info("지속신호 스케줄러 시작 - 대상 유저 수: {}", userIds.size());

        for (Long userId : userIds) {
            try {
                persistentSignalService.checkPersistentSignals(userId);
                persistentSignalService.checkWorseningSignals(userId);
            } catch (Exception e) {
                // 한 유저 처리 중 오류가 나도 나머지 유저 처리는 계속되도록 격리
                log.error("유저 {} 지속신호 판정 중 오류 발생", userId, e);
            }
        }

        log.info("지속신호 스케줄러 종료");
    }
}