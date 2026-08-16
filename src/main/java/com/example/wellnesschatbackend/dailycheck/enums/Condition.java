package com.example.wellnesschatbackend.dailycheck.enums;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;

/* 컨디션 선택지(great/good/okay/bad/awful) */
public enum Condition {
    GREAT("great"),
    GOOD("good"),
    OKAY("okay"),
    BAD("bad"),
    AWFUL("awful");

    private final String wireValue;

    Condition(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }

    public static Condition fromWireValue(String wireValue) {
        return Arrays.stream(values())
                .filter(condition -> condition.wireValue.equals(wireValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown condition: " + wireValue));
    }

    public static class Converter implements AttributeConverter<Condition, String> {
        @Override
        public String convertToDatabaseColumn(Condition attribute) {
            return attribute == null ? null : attribute.getWireValue();
        }

        @Override
        public Condition convertToEntityAttribute(String dbData) {
            return dbData == null ? null : Condition.fromWireValue(dbData);
        }
    }
}
