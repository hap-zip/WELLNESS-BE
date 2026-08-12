package com.example.wellnesschatbackend.wellnessdailyexpert.dailycheck.enums;

import jakarta.persistence.AttributeConverter;

import java.util.Arrays;

/* SelectableBodyMap.tsx: 'front' | 'back'(몸 앞, 뒤) */
public enum BodyView {
    FRONT("front"),
    BACK("back");

    private final String wireValue;

    BodyView(String wireValue) {
        this.wireValue = wireValue;
    }

    public String getWireValue() {
        return wireValue;
    }

    public static BodyView fromWireValue(String wireValue) {
        return Arrays.stream(values())
                .filter(view -> view.wireValue.equals(wireValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown body view: " + wireValue));
    }

    public static class Converter implements AttributeConverter<BodyView, String> {
        @Override
        public String convertToDatabaseColumn(BodyView attribute) {
            return attribute == null ? null : attribute.getWireValue();
        }

        @Override
        public BodyView convertToEntityAttribute(String dbData) {
            return dbData == null ? null : BodyView.fromWireValue(dbData);
        }
    }
}
