package com.example.wellnesschatbackend.wellnessdailyexpert.exception;

public record ApiError(String code, String message, String field) {

    public ApiError(String code, String message) {
        this(code, message, null);
    }
}
