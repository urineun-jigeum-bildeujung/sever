package com.golajugaenyang.common.core.exception;


import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


/**
 * 1) AppException - 정의한 비즈니스 예외 <br>
 * 2) MethodArgumentNotValidException - Bean Validation 실패 <br>
 * 3) 그 외 Spring MVC 내장 예외 - 부모 클래스가 ProblemDetail로 자동 처리 + errorCode 보강 <br>
 * 4) Exception (catch-all) - 예상하지 못한 모든 예외
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_CODE_PROPERTY = "errorCode";

    @ExceptionHandler(AppException.class)
    public ProblemDetail handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            errorCode.getHttpStatus(), e.getMessage()
        );
        problemDetail.setTitle(errorCode.getCode());
        problemDetail.setProperty(ERROR_CODE_PROPERTY, errorCode.getCode());

        log.warn("[AppException] code={}, message={}", errorCode.getCode(), e.getMessage());

        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status, @NonNull WebRequest request) {

        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.of(
                "field", fe.getField(),
                "reason", Objects.requireNonNullElse(fe.getDefaultMessage(), "유효하지 않은 값입니다.")
            ))
            .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            status, CommonErrorCode.INVALID_INPUT_VALUE.getMessage()
        );
        problemDetail.setProperty(ERROR_CODE_PROPERTY,
            CommonErrorCode.INVALID_INPUT_VALUE.getCode());
        problemDetail.setProperty("fieldErrors", fieldErrors);

        log.warn("[ValidationException] fieldErrors={}", fieldErrors);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(
        @Nullable Object body, @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode statusCode, @NonNull WebRequest request) {

        if (body instanceof ProblemDetail problemDetail && !hasErrorCode(problemDetail)) {
            problemDetail.setProperty(ERROR_CODE_PROPERTY, "COMMON_" + statusCode.value());
        }
        return super.createResponseEntity(body, headers, statusCode, request);
    }

    private boolean hasErrorCode(ProblemDetail problemDetail) {
        Map<String, Object> properties = problemDetail.getProperties();
        return properties != null && properties.containsKey(ERROR_CODE_PROPERTY);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception e, HttpServletRequest request) {
        log.error("[UnexpectedException] uri={}, message={}",
            request.getRequestURI(), e.getMessage(), e);

        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            errorCode.getHttpStatus(), errorCode.getMessage()
        );
        problemDetail.setTitle(errorCode.getCode());
        problemDetail.setProperty(ERROR_CODE_PROPERTY, errorCode.getCode());
        return problemDetail;
    }

}
