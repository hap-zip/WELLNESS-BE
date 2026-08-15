package com.example.wellnesschatbackend.wellnesschat.dto;

import java.util.Arrays;

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
                .orElse(UNKNOWN);
    }
}