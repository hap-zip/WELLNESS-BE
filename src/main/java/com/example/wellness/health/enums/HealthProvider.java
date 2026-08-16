package com.example.wellness.health.enums;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;

/** FE HealthConnectionSettings.provider: 'apple-health' | 'health-connect'. (manual은 연결 대상이 아니라 제외) */
public enum HealthProvider {
    APPLE_HEALTH("apple-health"),
    HEALTH_CONNECT("health-connect");

    private final String wireValue;

    HealthProvider(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }

    public static HealthProvider fromWireValue(String wireValue) {
        return Arrays.stream(values())
                .filter(provider -> provider.wireValue.equals(wireValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown health provider: " + wireValue));
    }

    public static class Converter implements AttributeConverter<HealthProvider, String> {
        @Override
        public String convertToDatabaseColumn(HealthProvider attribute) {
            return attribute == null ? null : attribute.getWireValue();
        }

        @Override
        public HealthProvider convertToEntityAttribute(String dbData) {
            return dbData == null ? null : HealthProvider.fromWireValue(dbData);
        }
    }
}
