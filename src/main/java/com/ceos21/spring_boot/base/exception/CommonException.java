package com.ceos21.spring_boot.base.exception;

import com.ceos21.spring_boot.base.ApiResponse;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class CommonException {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
        log.error("Custom Error: {} - Code: {} - Status: {}",
                ex.getMessage(),
                ex.getClass().getSimpleName(),
                ex.getErrorStatus().getHttpStatus());

        return CustomException.createErrorResponse(ex.getErrorStatus(), null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage()
                ));
        log.error("Validation Error: {}", errors);
        return CustomException.createErrorResponse(ErrorStatus.COMMON_BAD_REQUEST, errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage()
                ));
        log.error("Validation Error: {}", errors);
        return CustomException.createErrorResponse(ErrorStatus.COMMON_BAD_REQUEST, errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpectedException(Exception ex) {
        log.error("Unexpected Error: ", ex);
        return CustomException.createErrorResponse(ErrorStatus.COMMON_INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}