package com.example.wellnesschatbackend.wellnesschat.service;

import com.example.wellnesschatbackend.wellnesschat.dto.TriggerType;
import com.example.wellnesschatbackend.wellnesschat.entity.PersistentSignal;
import com.example.wellnesschatbackend.wellnesschat.repository.PersistentSignalRepository;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.entity.DailyCheck;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.entity.DailyCheckPainArea;
import com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.repository.DailyCheckRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersistentSignalServiceTest {

    private final DailyCheckRepository dailyCheckRepository = mock(DailyCheckRepository.class);
    private final PersistentSignalRepository persistentSignalRepository = mock(PersistentSignalRepository.class);
    private final PersistentSignalService service = new PersistentSignalService(
            dailyCheckRepository, persistentSignalRepository, 7, 4, 1.5
    );

    @Test
    void 같은_부위가_4일_이상_기록되면_지속신호가_생성된다() throws Exception {
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        // "back-neck" 부위가 5일치 기록에 등장하는 상황 구성
        List<DailyCheck> checks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DailyCheck check = new DailyCheck();
            setPainAreas(check, List.of(painArea("back-neck", 3)));
            checks.add(check);
        }

        when(dailyCheckRepository.findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(eq(userId), any(), any()))
                .thenReturn(checks);
        when(persistentSignalRepository.findByUserIdAndPainAreaAndTriggerTypeAndResolvedAtIsNull(
                eq(userId), eq("back-neck"), eq(TriggerType.PERSISTENT)))
                .thenReturn(Optional.empty());
        when(persistentSignalRepository.save(any(PersistentSignal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<PersistentSignal> result = service.checkPersistentSignals(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPainArea()).isEqualTo("back-neck");
        assertThat(result.get(0).getTriggerType()).isEqualTo(TriggerType.PERSISTENT);
        assertThat(result.get(0).getStreakDays()).isEqualTo(5);
    }

    @Test
    void 기준_미만이면_신호가_생성되지_않는다() {
        Long userId = 1L;

        List<DailyCheck> checks = new ArrayList<>();
        try {
            for (int i = 0; i < 2; i++) { // 2일치만, 기준(4일) 미달
                DailyCheck check = new DailyCheck();
                setPainAreas(check, List.of(painArea("back-neck", 3)));
                checks.add(check);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(dailyCheckRepository.findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(eq(userId), any(), any()))
                .thenReturn(checks);

        List<PersistentSignal> result = service.checkPersistentSignals(userId);

        assertThat(result).isEmpty();
        verify(persistentSignalRepository, never()).save(any());
    }

    @Test
    void 뒤쪽_통증강도가_앞쪽보다_1_5점_이상_높으면_악화신호가_생성된다() {
        Long userId = 1L;
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6); // lookbackDays=7 기준 7일 전부터

        List<DailyCheck> checks = new ArrayList<>();

        // 앞쪽 3일: intensity 1
        for (int i = 0; i < 3; i++) {
            DailyCheck check = new DailyCheck();
            check.setCheckDate(start.plusDays(i));
            setPainAreas(check, List.of(painArea("back-neck", 1)));
            checks.add(check);
        }
        // 뒤쪽 4일: intensity 4 (차이 = 3.0, 기준 1.5 이상)
        for (int i = 3; i < 7; i++) {
            DailyCheck check = new DailyCheck();
            check.setCheckDate(start.plusDays(i));
            setPainAreas(check, List.of(painArea("back-neck", 4)));
            checks.add(check);
        }

        when(dailyCheckRepository.findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(eq(userId), any(), any()))
                .thenReturn(checks);
        when(persistentSignalRepository.findByUserIdAndPainAreaAndTriggerTypeAndResolvedAtIsNull(
                eq(userId), eq("back-neck"), eq(TriggerType.WORSENING)))
                .thenReturn(Optional.empty());
        when(persistentSignalRepository.save(any(PersistentSignal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<PersistentSignal> result = service.checkWorseningSignals(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPainArea()).isEqualTo("back-neck");
        assertThat(result.get(0).getTriggerType()).isEqualTo(TriggerType.WORSENING);
    }

    @Test
    void 강도_상승폭이_기준_미만이면_악화신호가_생성되지_않는다() {
        Long userId = 1L;
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);

        List<DailyCheck> checks = new ArrayList<>();

        // 앞쪽 3일: intensity 2
        for (int i = 0; i < 3; i++) {
            DailyCheck check = new DailyCheck();
            check.setCheckDate(start.plusDays(i));
            setPainAreas(check, List.of(painArea("back-neck", 2)));
            checks.add(check);
        }
        // 뒤쪽 4일: intensity 2.5 정도 차이만 (기준 1.5 미달)
        for (int i = 3; i < 7; i++) {
            DailyCheck check = new DailyCheck();
            check.setCheckDate(start.plusDays(i));
            setPainAreas(check, List.of(painArea("back-neck", 3)));
            checks.add(check);
        }

        when(dailyCheckRepository.findByUserIdAndCheckDateBetweenOrderByCheckDateAsc(eq(userId), any(), any()))
                .thenReturn(checks);

        List<PersistentSignal> result = service.checkWorseningSignals(userId);

        assertThat(result).isEmpty();
    }

    private DailyCheckPainArea painArea(String zoneId, int intensity) {
        DailyCheckPainArea area = new DailyCheckPainArea();
        area.setZoneId(zoneId);
        area.setIntensity(intensity);
        return area;
    }

    // painAreas는 replacePainAreas()를 통해서만 세팅 가능하므로 그걸 사용
    private void setPainAreas(DailyCheck check, List<DailyCheckPainArea> areas) {
        check.replacePainAreas(areas);
    }
}