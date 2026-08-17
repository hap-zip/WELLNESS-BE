package com.example.wellness.connectionview.service;

import com.example.wellness.dailycheck.entity.DailyCheck;
import com.example.wellness.connectionview.entity.PatternEntity;
import com.example.wellness.dailycheck.repository.DailyCheckRepository;
import com.example.wellness.connectionview.repository.PatternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatternAnalysisService {
    private final DailyCheckRepository dailyCheckRepository;
    private final PatternRepository patternRepository;

    @Transactional
    public void analyzeUserPatterns(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        List<DailyCheck> records = dailyCheckRepository.findAllByUserIdAndCheckDateBetween(userId, startDate, endDate);
        if (records.size() < 30) return;
        List<PatternEntity> discoveredPatterns = new ArrayList<>();
        Map<String, Function<DailyCheck, Number>> numericMetrics = getNumericMetrics(records);
        Map<String, Function<DailyCheck, String>> categoricalMetrics = getCategoricalMetrics();

        discoveredPatterns.addAll(analyzeAssociations(userId, records, numericMetrics, startDate, endDate));
        discoveredPatterns.addAll(analyzeConditions(userId, records, categoricalMetrics, numericMetrics, startDate, endDate));
        if (!discoveredPatterns.isEmpty())
            patternRepository.saveAll(discoveredPatterns);
    }
    private List<PatternEntity> analyzeAssociations(Long userId, List<DailyCheck> records, Map<String, Function<DailyCheck, Number>> numericMetrics, LocalDate startDate, LocalDate endDate) {
        List<PatternEntity> patterns = new ArrayList<>();
        List<String> keys = new ArrayList<>(numericMetrics.keySet());
        for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                String sourceKey = keys.get(i);
                String targetKey = keys.get(j);
                List<Double> xValues = new ArrayList<>();
                List<Double> yValues = new ArrayList<>();
                for (DailyCheck dc : records) {
                    Number x = numericMetrics.get(sourceKey).apply(dc);
                    Number y = numericMetrics.get(targetKey).apply(dc);
                    if (x != null && y != null) {
                        xValues.add(x.doubleValue());
                        yValues.add(y.doubleValue());
                    }
                }
                if (xValues.size() < 30) continue;
                double correlation = calculatePearsonCorrelation(xValues, yValues);
                if (Math.abs(correlation) >= 0.5) {
                    PatternEntity.RelationDirection direction = correlation > 0
                            ? PatternEntity.RelationDirection.POSITIVE
                            : PatternEntity.RelationDirection.NEGATIVE;
                    PatternEntity.PatternStatus status = determineStatus(xValues.size());
                    patterns.add(createPatternEntity(userId, sourceKey, targetKey, PatternEntity.PatternType.ASSOCIATION, direction, status, startDate, endDate));
                }
            }
        }
        return patterns;
    }
    private List<PatternEntity> analyzeConditions(Long userId, List<DailyCheck> records, Map<String, Function<DailyCheck, String>> categoricalMetrics,
                                                  Map<String, Function<DailyCheck, Number>> numericMetrics, LocalDate startDate, LocalDate endDate) {
        List<PatternEntity> patterns = new ArrayList<>();
        for (String catKey : categoricalMetrics.keySet()) {
            for (String numKey : numericMetrics.keySet()) {
                Map<String, List<DailyCheck>> grouped = records.stream()
                        .filter(dc -> categoricalMetrics.get(catKey).apply(dc) != null)
                        .filter(dc -> numericMetrics.get(numKey).apply(dc) != null)
                        .collect(Collectors.groupingBy(categoricalMetrics.get(catKey)));
                double totalAvg = records.stream()
                        .map(numericMetrics.get(numKey))
                        .filter(Objects::nonNull)
                        .mapToDouble(Number::doubleValue)
                        .average()
                        .orElse(0.0);
                for (Map.Entry<String, List<DailyCheck>> entry : grouped.entrySet()) {
                    String categoryValue = entry.getKey();
                    List<DailyCheck> subset = entry.getValue();
                    if (subset.size() < 30) continue;
                    double subsetAvg = subset.stream()
                            .map(numericMetrics.get(numKey))
                            .mapToDouble(Number::doubleValue)
                            .average()
                            .orElse(0.0);
                    if (Math.abs(subsetAvg - totalAvg) >= 1.0) {
                        PatternEntity.RelationDirection direction = subsetAvg > totalAvg
                                ? PatternEntity.RelationDirection.POSITIVE
                                : PatternEntity.RelationDirection.NEGATIVE;
                        PatternEntity.PatternStatus status = determineStatus(subset.size());
                        String detailedSourceKey = catKey + ":" + categoryValue;
                        patterns.add(createPatternEntity(userId, detailedSourceKey, numKey, PatternEntity.PatternType.CONDITION, direction, status, startDate, endDate));
                    }
                }
            }
        }
        return patterns;
    }
    private Map<String, Function<DailyCheck, Number>> getNumericMetrics(List<DailyCheck> records) {
        Map<String, Function<DailyCheck, Number>> map = new HashMap<>();
        map.put("sleep_duration", DailyCheck::getAutoSleepDurationMinutes);
        map.put("sleep_satisfaction", DailyCheck::getSleepSatisfaction);
        map.put("auto_steps", DailyCheck::getAutoSteps);
        Set<String> allPainAreas = records.stream()
                .filter(dc -> dc.getPainAreas() != null)
                .flatMap(dc -> dc.getPainAreas().stream())
                .map(area -> area.getZoneId())
                .collect(Collectors.toSet());
        for (String zoneId : allPainAreas) {
            String metricKey = "pain_" + zoneId;
            map.put(metricKey, dc -> {
                if (dc.getPainAreas() == null) return null;
                return dc.getPainAreas().stream()
                        .filter(a -> zoneId.equals(a.getZoneId()))
                        .map(a -> (Number) a.getIntensity())
                        .findFirst()
                        .orElse(null);
            });
        }
        return map;
    }
    private Map<String, Function<DailyCheck, String>> getCategoricalMetrics() {
        Map<String, Function<DailyCheck, String>> map = new HashMap<>();
        map.put("condition", dc -> dc.getCondition() != null ? dc.getCondition().name() : null);
        map.put("sleep_posture", dc -> dc.getSleepPosture() != null ? dc.getSleepPosture().name() : null);
        map.put("pillow_height", DailyCheck::getSleepPillow);
        return map;
    }
    private String generatePatternName(String source, String target, PatternEntity.PatternType type, PatternEntity.RelationDirection direction) {
        String sourceName = translateMetricName(source);
        String targetName = translateMetricName(target);
        if (type == PatternEntity.PatternType.CONDITION) {
            String conditionAction = source.contains(":") ? " 수면 시 " : " 시 ";
            String dirStr = direction == PatternEntity.RelationDirection.POSITIVE ? "증가" : "감소";
            return sourceName + conditionAction + targetName + " " + dirStr;
        }
        else {
            if (direction == PatternEntity.RelationDirection.NEGATIVE && source.contains("sleep_duration"))
                return "수면 부족 시 " + targetName + " 증가";
            if (direction == PatternEntity.RelationDirection.POSITIVE)
                return sourceName + "과(와) " + targetName + "이(가) 함께 움직여요";
            return sourceName + " 감소 시 " + targetName + " 증가";
        }
    }
    private String translateMetricName(String metric) {
        if (metric.startsWith("pain_")) {
            String zoneId = metric.replace("pain_", "");
            String koreanArea = switch (zoneId) {
                case "front-head" -> "앞머리";
                case "front-shoulder-left" -> "왼쪽 앞어깨";
                case "front-shoulder-right" -> "오른쪽 앞어깨";
                case "front-arm-left" -> "왼쪽 앞팔";
                case "front-arm-right" -> "오른쪽 앞팔";
                case "front-chest" -> "가슴";
                case "front-abdomen" -> "복부";
                case "front-leg-left" -> "왼쪽 앞다리";
                case "front-leg-right" -> "오른쪽 앞다리";
                case "back-head" -> "뒷머리";
                case "back-neck" -> "뒷목";
                case "back-shoulder-left" -> "왼쪽 등/어깨";
                case "back-shoulder-right" -> "오른쪽 등/어깨";
                case "back-arm-left" -> "왼쪽 뒷팔";
                case "back-arm-right" -> "오른쪽 뒷팔";
                case "back-upper" -> "등 상단";
                case "back-lower" -> "허리";
                case "back-leg-left" -> "왼쪽 뒷다리";
                case "back-leg-right" -> "오른쪽 뒷다리";
                default -> zoneId;
            };
            return koreanArea + " 불편";
        }
        if (metric.startsWith("sleep_posture:"))
            return metric.replace("sleep_posture:", "");
        if (metric.startsWith("pillow_height:"))
            return "베개 높이 " + metric.replace("pillow_height:", "");
        if (metric.startsWith("condition:"))
            return "컨디션 " + metric.replace("condition:", "");
        return switch (metric) {
            case "sleep_duration" -> "수면 시간";
            case "sleep_satisfaction" -> "수면 만족도";
            case "auto_steps" -> "걸음 수";
            default -> metric;
        };
    }
    private PatternEntity createPatternEntity(Long userId, String source, String target, PatternEntity.PatternType type,
                                              PatternEntity.RelationDirection direction, PatternEntity.PatternStatus status,
                                              LocalDate start, LocalDate end) {
        String generatedName = generatePatternName(source, target, type, direction);
        return PatternEntity.builder()
                .userId(userId)
                .patternName(generatedName)
                .patternType(type)
                .sourceMetric(source)
                .targetMetric(target)
                .relationDirection(direction)
                .status(status)
                .analysisStartDate(start)
                .analysisEndDate(end)
                .build();
    }
    private PatternEntity.PatternStatus determineStatus(int count) {
        if (count < 5) return PatternEntity.PatternStatus.INSUFFICIENT;
        if (count < 14) return PatternEntity.PatternStatus.POSSIBLE;
        return PatternEntity.PatternStatus.CONFIRMED;
    }
    private double calculatePearsonCorrelation(List<Double> x, List<Double> y) {
        int n = x.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumSqX = 0, sumSqY = 0;
        for (int i = 0; i < n; i++) {
            sumX += x.get(i);
            sumY += y.get(i);
            sumXY += x.get(i) * y.get(i);
            sumSqX += Math.pow(x.get(i), 2);
            sumSqY += Math.pow(y.get(i), 2);
        }
        double numerator = (n * sumXY) - (sumX * sumY);
        double denominator = Math.sqrt(((n * sumSqX) - Math.pow(sumX, 2)) * ((n * sumSqY) - Math.pow(sumY, 2)));
        return denominator == 0 ? 0 : numerator / denominator;
    }
}