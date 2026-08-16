package com.example.wellnesschatbackend.wellnesschat.mock;

import com.example.wellnesschatbackend.wellnesschat.dto.DailyCheck;

import java.util.List;

/**
 * 영인의 daily_checks API가 나오면 이 인터페이스의 실제 구현체(REST 클라이언트)로
 * MockDailyCheckProvider를 갈아끼우기만 하면 되도록 시그니처를 미리 맞춰둔다.
 */
public interface DailyCheckProvider {
    List<DailyCheck> getRecentChecks(Long userId, int days);
}
