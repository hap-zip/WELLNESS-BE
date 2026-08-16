package com.example.wellness.wellnesschat.controller;

import com.example.wellness.wellnesschat.dto.PersistentSignalResponse;
import com.example.wellness.wellnesschat.entity.PersistentSignal;
import com.example.wellness.wellnesschat.service.PersistentSignalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * X-User-Id 헤더는 인증 모듈이 붙기 전까지 쓰는 임시 표시자.
 */
@Tag(name = "지속신호안내", description = "daily-check 기록 기반 지속신호 감지 API")
@RestController
@RequestMapping("/api/v1/persistent-signals")
public class PersistentSignalController {

    private final PersistentSignalService persistentSignalService;

    public PersistentSignalController(PersistentSignalService persistentSignalService) {
        this.persistentSignalService = persistentSignalService;
    }

    @Operation(
            summary = "지속신호 검사 실행",
            description = "최근 7일 daily-check 기록을 바탕으로 지속(반복) 신호를 검사하고, "
                    + "조건 충족 시 새 신호를 저장합니다. 이미 활성화된 같은 부위 신호는 중복 생성하지 않습니다."
    )
    @GetMapping("/check")
    public List<PersistentSignalResponse> check(@RequestHeader("X-User-Id") Long userId) {
        List<PersistentSignal> signals = persistentSignalService.checkPersistentSignals(userId);
        return signals.stream()
                .map(this::toResponse)
                .toList();
    }

    @Operation(
            summary = "악화신호 검사 실행",
            description = "최근 7일을 앞/뒤로 나눠 평균 통증 강도 상승폭을 비교하여 악화 신호를 검사합니다."
    )
    @GetMapping("/check-worsening")
    public List<PersistentSignalResponse> checkWorsening(@RequestHeader("X-User-Id") Long userId) {
        List<PersistentSignal> signals = persistentSignalService.checkWorseningSignals(userId);
        return signals.stream()
                .map(this::toResponse)
                .toList();
    }

    private PersistentSignalResponse toResponse(PersistentSignal signal) {
        return new PersistentSignalResponse(
                signal.getId(),
                signal.getPainArea(),
                signal.getTriggerType().name(),
                signal.getStreakDays(),
                signal.getMessageSent(),
                signal.getTriggeredAt()
        );
    }
}