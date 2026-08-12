package com.example.wellnesschatbackend.wellnessdailyexpert.expertcard.enums;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;

/* '3days' | '7days' | '14days' | 'custom' */
public enum ReportPeriod {
    THREE_DAYS("3days"),
    SEVEN_DAYS("7days"),
    FOURTEEN_DAYS("14days"),
    CUSTOM("custom");

    private final String wireValue;

    ReportPeriod(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }

    public static ReportPeriod fromWireValue(String wireValue) {
        return Arrays.stream(values())
                .filter(period -> period.wireValue.equals(wireValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown report period: " + wireValue));
    }

    public static class Converter implements AttributeConverter<ReportPeriod, String> {
        @Override
        public String convertToDatabaseColumn(ReportPeriod attribute) {
            return attribute == null ? null : attribute.getWireValue();
        }

        @Override
        public ReportPeriod convertToEntityAttribute(String dbData) {
            return dbData == null ? null : ReportPeriod.fromWireValue(dbData);
        }
    }
}
