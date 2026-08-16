package com.example.wellnesschatbackend.wellnesschat.dto;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;

/**
 * persistent_signals.trigger_type 매핑.
 * 지속(반복) / 악화(강도상승) / 무개선(루틴 후 미개선) 3가지.
 */
public enum TriggerType {
    PERSISTENT("지속"),
    WORSENING("악화"),
    NO_IMPROVEMENT("무개선");

    private final String wireValue;

    TriggerType(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }

    public static TriggerType fromWireValue(String wireValue) {
        return Arrays.stream(values())
                .filter(type -> type.wireValue.equals(wireValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown trigger type: " + wireValue));
    }

    public static class Converter implements AttributeConverter<TriggerType, String> {
        @Override
        public String convertToDatabaseColumn(TriggerType attribute) {
            return attribute == null ? null : attribute.name(); // DB엔 영문 그대로 저장 (PERSISTENT/WORSENING/NO_IMPROVEMENT)
        }

        @Override
        public TriggerType convertToEntityAttribute(String dbData) {
            return dbData == null ? null : TriggerType.valueOf(dbData);
        }
    }
}