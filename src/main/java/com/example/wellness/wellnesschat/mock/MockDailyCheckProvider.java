package com.example.wellness.wellnesschat.mock;

import com.example.wellness.wellnesschat.dto.DailyCheck;
import com.example.wellness.wellnesschat.dto.SleepPosture;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 영인·서진의 실제 API가 나오기 전까지 쓰는 mock 데이터.
 * "엎드려 잠(PRONE) -> 목 뻐근함(neckPainScore) 상승" 패턴을 의도적으로 심어서
 * 가드레일/프롬프트 로직이 실제 상관관계를 잘 짚어내는지 검증할 수 있게 했다.
 *
 * TODO: 영인 daily_checks API 나오면 이 클래스를 실제 REST 클라이언트 구현체로 교체.
 *       DailyCheckProvider 인터페이스 시그니처는 유지.
 */
@Component
public class MockDailyCheckProvider implements DailyCheckProvider {
    @Override
    public List<DailyCheck> getRecentChecks(Long userId, int days) {
        LocalDate today = LocalDate.now();
        List<DailyCheck> fixture = new ArrayList<>();
        fixture.add(new DailyCheck(today.minusDays(6), 7.5, SleepPosture.SUPINE, "적당한 높이", 2, null));
        fixture.add(new DailyCheck(today.minusDays(5), 6.0, SleepPosture.PRONE, "높은 편", 6, "아침에 목이 뻐근했음"));
        fixture.add(new DailyCheck(today.minusDays(4), 7.0, SleepPosture.LEFT_SIDE, "적당한 높이", 3, null));
        fixture.add(new DailyCheck(today.minusDays(3), 5.5, SleepPosture.PRONE, "높은 편", 7, "베개가 너무 높았던 듯"));
        fixture.add(new DailyCheck(today.minusDays(2), 6.2, SleepPosture.PRONE, "높은 편", 6, null));
        fixture.add(new DailyCheck(today.minusDays(1), 7.8, SleepPosture.RIGHT_SIDE, "낮은 편", 2, null));
        fixture.add(new DailyCheck(today, 6.5, SleepPosture.PRONE, "높은 편", 7, "목이 뻐근해서 뒤척임"));
        return fixture.stream()
                .sorted(Comparator.comparing(DailyCheck::date).reversed())
                .limit(Math.max(days, 0))
                .toList();
    }
}