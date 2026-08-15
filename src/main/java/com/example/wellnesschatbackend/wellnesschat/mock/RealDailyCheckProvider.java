package com.example.wellnesschatbackend.wellnesschat.mock;

import com.example.wellnesschatbackend.wellnesschat.dto.DailyCheck;
import com.example.wellnesschatbackend.wellnesschat.dto.SleepPosture;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.dto.response.DailyCheckResponse;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.service.DailyCheckService;
import com.example.wellnesschatbackend.wellnessdailyexpert.exception.NotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 영인 daily_checks API를 실제로 호출하는 구현체.
 * 같은 스프링 컨텍스트 안이라 HTTP 대신 DailyCheckService를 직접 주입받아 호출한다.
 */
@Primary
@Component
public class RealDailyCheckProvider implements DailyCheckProvider {

    private static final String NECK_ZONE_ID = "back-neck";

    private final DailyCheckService dailyCheckService;

    public RealDailyCheckProvider(DailyCheckService dailyCheckService) {
        this.dailyCheckService = dailyCheckService;
    }

    @Override
    public List<DailyCheck> getRecentChecks(Long userId, int days) {
        List<DailyCheck> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < days; i++) {
            LocalDate date = today.minusDays(i);
            try {
                DailyCheckResponse response = dailyCheckService.getDetail(userId, date);
                result.add(toDailyCheck(response, date));
            } catch (NotFoundException e) {
                // 그 날짜에 기록이 없으면 건너뜀
            }
        }
        return result;
    }

    private DailyCheck toDailyCheck(DailyCheckResponse response, LocalDate date) {
        double sleepHours = response.autoRecords() != null && response.autoRecords().sleepDurationMinutes() != null
                ? response.autoRecords().sleepDurationMinutes() / 60.0
                : 0.0;

        SleepPosture posture = response.sleep() != null && response.sleep().posture() != null
                ? SleepPosture.fromWireValue(response.sleep().posture())
                : SleepPosture.UNKNOWN;

        String pillowDescription = response.sleep() != null ? response.sleep().pillow() : null;

        int neckPainScore = response.painAreas() == null ? 0 : response.painAreas().stream()
                .filter(p -> NECK_ZONE_ID.equals(p.zoneId()))
                .mapToInt(p -> p.intensity() == null ? 0 : p.intensity())
                .max()
                .orElse(0);

        return new DailyCheck(date, sleepHours, posture, pillowDescription, neckPainScore, response.memo());
    }
}