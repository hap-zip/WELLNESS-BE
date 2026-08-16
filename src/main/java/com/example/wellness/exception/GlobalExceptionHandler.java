package com.example.wellness.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.DateTimeException;

/**
 * 예외마다 별도 핸들러 + 고유 ErrorCode를 매핑해 어떤 상황인지 응답만 보고 구분할 수 있게 함.
 * wellnessdailyexpert 패키지 컨트롤러에만 적용되도록 스코프 제한 — wellnesschat은 스프링 기본 처리를 그대로 쓴다.
 */
@RestControllerAdvice(basePackages = "com.example.wellnesschatbackend.wellnessdailyexpert")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException exception) {
        ErrorCode code = exception.getErrorCode();
        return ResponseEntity.status(code.getStatus()).body(new ApiError(code.name(), exception.getMessage()));
    }

    /** @Valid로 걸리는 DTO 필드 검증 실패 (@NotBlank, @Min 등) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String field = fieldError == null ? null : fieldError.getField();
        String message = fieldError == null ? "입력값을 확인해 주세요." : fieldError.getDefaultMessage();
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
                .body(new ApiError(ErrorCode.VALIDATION_FAILED.name(), message, field));
    }

    /** @RequestParam/@PathVariable에 직접 붙인 제약(@Min 등) 위반 */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException exception) {
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
                .body(new ApiError(ErrorCode.VALIDATION_FAILED.name(), exception.getMessage()));
    }

    /** 서비스 로직에서 던지는 비즈니스 규칙 위반 (예: custom 기간인데 날짜 누락, endDate < startDate) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(ErrorCode.INVALID_ARGUMENT.getStatus())
                .body(new ApiError(ErrorCode.INVALID_ARGUMENT.name(), exception.getMessage()));
    }

    /** 요청 본문이 JSON으로 파싱조차 안 되는 경우 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedBody(HttpMessageNotReadableException exception) {
        return ResponseEntity.status(ErrorCode.MALFORMED_REQUEST_BODY.getStatus())
                .body(new ApiError(ErrorCode.MALFORMED_REQUEST_BODY.name(), "요청 본문 형식이 올바르지 않아요."));
    }

    /** 경로·쿼리 파라미터 타입이 안 맞는 경우 (예: {date}에 2026-13-40, year=abc) */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = exception.getName() + " 값의 형식이 올바르지 않아요: " + exception.getValue();
        return ResponseEntity.status(ErrorCode.TYPE_MISMATCH.getStatus())
                .body(new ApiError(ErrorCode.TYPE_MISMATCH.name(), message, exception.getName()));
    }

    /** 필수 @RequestHeader(예: X-User-Id) 누락. 안 잡으면 catch-all에 걸려 500으로 잘못 나간다. */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingHeader(MissingRequestHeaderException exception) {
        String message = exception.getHeaderName() + " 헤더가 필요해요.";
        return ResponseEntity.status(ErrorCode.MISSING_HEADER.getStatus())
                .body(new ApiError(ErrorCode.MISSING_HEADER.name(), message, exception.getHeaderName()));
    }

    /**
     * LocalDate.parse(문자열 형식 오류)와 LocalDate.of(month=13 같은 범위 오류) 둘 다 이 예외의 하위 타입이라
     * 한 핸들러로 같이 잡는다. 안 잡으면 catch-all에 걸려 500으로 잘못 나간다.
     */
    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<ApiError> handleDateTimeException(DateTimeException exception) {
        return ResponseEntity.status(ErrorCode.INVALID_ARGUMENT.getStatus())
                .body(new ApiError(ErrorCode.INVALID_ARGUMENT.name(), "날짜 값이 올바르지 않아요: " + exception.getMessage()));
    }

    /** 위에서 못 잡은 나머지 — 스택트레이스가 그대로 응답에 새는 것만 막는 최후의 방어선 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        log.error("처리 못한 예외 발생", exception);
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(new ApiError(ErrorCode.INTERNAL_ERROR.name(), "예상하지 못한 오류가 발생했어요."));
    }
}
