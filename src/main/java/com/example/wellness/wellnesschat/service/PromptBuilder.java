package com.example.wellness.wellnesschat.service;

import com.example.wellness.wellnesschat.dto.DailyCheck;
import com.example.wellness.wellnesschat.dto.SleepPosture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;

/**
 * 시스템 프롬프트 템플릿(resources/prompts/system-prompt-template.txt)에
 * 사용자의 실제(mock) 기록 요약을 끼워 넣어 최종 시스템 프롬프트를 만든다.
 *
 * 템플릿을 코드 밖 텍스트 파일로 뺀 이유: 프롬프트 문구만 바꿀 때 재컴파일 없이
 * 팀이 바로 수정/리뷰할 수 있게 하기 위함 (웰니스챗_프롬프트_설계.md 산출물과 1:1 대응).
 */
@Component
public class PromptBuilder {

    private static final String TEMPLATE_PATH = "prompts/system-prompt-template.txt";
    private static final String PLACEHOLDER = "{{DAILY_CHECK_SUMMARY}}";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd");

    private final int minDaysForPattern;
    private final String templateRaw;

    public PromptBuilder(@Value("${wellness.daily-check.min-days-for-pattern:7}") int minDaysForPattern) {
        this.minDaysForPattern = minDaysForPattern;
        this.templateRaw = loadTemplate();
    }

    public String buildSystemPrompt(List<DailyCheck> checks) {
        return templateRaw.replace(PLACEHOLDER, buildDailyCheckSummary(checks));
    }

    private String buildDailyCheckSummary(List<DailyCheck> checks) {
        if (checks == null || checks.isEmpty()) {
            return "기록 없음. 아직 아무 데이터도 없으니 패턴을 절대 지어내지 말 것.";
        }

        StringBuilder sb = new StringBuilder();
        for (DailyCheck c : checks) {
            sb.append("- ")
                    .append(c.date().format(DATE_FMT))
                    .append(": 수면 ").append(c.sleepHours()).append("시간, 자세=")
                    .append(c.sleepPosture())
                    .append(", 목뻐근함 점수=").append(c.neckPainScore()).append("/10");
            if (c.notes() != null && !c.notes().isBlank()) {
                sb.append(", 메모=\"").append(c.notes()).append("\"");
            }
            sb.append("\n");
        }

        if (checks.size() < minDaysForPattern) {
            sb.append(String.format(
                    Locale.KOREA,
                    "(참고: 총 %d일치 기록으로, 패턴 판단 최소 기준인 %d일에는 못 미침. 패턴 단정 금지.)",
                    checks.size(), minDaysForPattern));
            return sb.toString();
        }

        OptionalDouble pronePain = checks.stream()
                .filter(c -> c.sleepPosture() == SleepPosture.PRONE)
                .mapToInt(DailyCheck::neckPainScore)
                .average();
        OptionalDouble otherPain = checks.stream()
                .filter(c -> c.sleepPosture() != SleepPosture.PRONE)
                .mapToInt(DailyCheck::neckPainScore)
                .average();
        long proneDays = checks.stream().filter(c -> c.sleepPosture() == SleepPosture.PRONE).count();

        if (pronePain.isPresent() && otherPain.isPresent() && proneDays > 0) {
            sb.append(String.format(
                    Locale.KOREA,
                    "(집계: 최근 %d일 중 %d일 엎드려 잠. 엎드려 잔 날 평균 목뻐근함 %.1f점 vs 그 외 %.1f점. 표본 %d일 기준.)",
                    checks.size(), proneDays, pronePain.getAsDouble(), otherPain.getAsDouble(), checks.size()));
        }

        return sb.toString();
    }

    private String loadTemplate() {
        try (InputStream is = new ClassPathResource(TEMPLATE_PATH).getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("시스템 프롬프트 템플릿을 못 읽었음: " + TEMPLATE_PATH, e);
        }
    }
}
