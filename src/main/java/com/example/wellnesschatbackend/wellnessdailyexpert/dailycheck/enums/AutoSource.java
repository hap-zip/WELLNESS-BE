package com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.enums;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;

/** 'apple-health' | 'health-connect' | 'manual' */
public enum AutoSource {
    APPLE_HEALTH("apple-health"),
    HEALTH_CONNECT("health-connect"),
    MANUAL("manual");

    private final String wireValue;

    AutoSource(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }

    public static AutoSource fromWireValue(String wireValue) {
        return Arrays.stream(values())
                .filter(source -> source.wireValue.equals(wireValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown auto source: " + wireValue));
    }

    public static class Converter implements AttributeConverter<AutoSource, String> {
        @Override
        public String convertToDatabaseColumn(AutoSource attribute) {
            return attribute == null ? null : attribute.getWireValue();
        }

        @Override
        public AutoSource convertToEntityAttribute(String dbData) {
            return dbData == null ? null : AutoSource.fromWireValue(dbData);
        }
    }
}
