package com.example.wellnesschatbackend.wellnesschat.mock;

import com.example.wellnesschatbackend.wellnesschat.dto.DailyCheck;
import com.example.wellnesschatbackend.wellnesschat.dto.SleepPosture;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * mock에 심어둔 "엎드려 잠(PRONE) -> 목 뻐근함 상승" 패턴이 실제로 존재하는지 검증.
 * 이게 깨지면 PromptBuilder가 뽑아내는 상관관계 문구도 같이 깨짐.
 */
class MockDailyCheckProviderTest {

    private final MockDailyCheckProvider provider = new MockDailyCheckProvider();

    @Test
    void 최근_7일치를_요청하면_7건이_최신순으로_와야한다() {
        List<DailyCheck> checks = provider.getRecentChecks(1L, 7);
        assertThat(checks).hasSize(7);
        assertThat(checks).isSortedAccordingTo((a, b) -> b.date().compareTo(a.date()));
    }

    @Test
    void 엎드려잔_날이_그렇지_않은_날보다_목뻐근함_평균이_높아야한다() {
        List<DailyCheck> checks = provider.getRecentChecks(1L, 7);
        double proneAvg = checks.stream()
                .filter(c -> c.sleepPosture() == SleepPosture.PRONE)
                .mapToInt(DailyCheck::neckPainScore)
                .average()
                .orElseThrow();
        double otherAvg = checks.stream()
                .filter(c -> c.sleepPosture() != SleepPosture.PRONE)
                .mapToInt(DailyCheck::neckPainScore)
                .average()
                .orElseThrow();
        assertThat(proneAvg).isGreaterThan(otherAvg);
    }
}