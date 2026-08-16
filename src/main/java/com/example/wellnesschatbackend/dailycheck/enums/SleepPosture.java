package com.example.wellnesschatbackend.dailycheck.enums;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;

/* POSTURES 배열(7개 고정값) */
public enum SleepPosture {
    SUPINE("똑바로"),
    LEFT_SIDE("왼쪽으로"),
    RIGHT_SIDE("오른쪽으로"),
    PRONE("엎드려서"),
    FETAL("웅크려서"),
    RECLINED("상체를 세우고"),
    UNKNOWN("잘 모르겠어요");

    private final String wireValue;

    SleepPosture(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }

    public static SleepPosture fromWireValue(String wireValue) {
        return Arrays.stream(values())
                .filter(posture -> posture.wireValue.equals(wireValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown sleep posture: " + wireValue));
    }

    public static class Converter implements AttributeConverter<SleepPosture, String> {
        @Override
        public String convertToDatabaseColumn(SleepPosture attribute) {
            return attribute == null ? null : attribute.getWireValue();
        }

        @Override
        public SleepPosture convertToEntityAttribute(String dbData) {
            return dbData == null ? null : SleepPosture.fromWireValue(dbData);
        }
    }
}
