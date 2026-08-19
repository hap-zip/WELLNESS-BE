package com.example.wellness.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    DAILY_CHECK_NOT_FOUND(HttpStatus.NOT_FOUND),
    EXPERT_CARD_NOT_FOUND(HttpStatus.NOT_FOUND),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST),
    MALFORMED_REQUEST_BODY(HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST),
    MISSING_HEADER(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
