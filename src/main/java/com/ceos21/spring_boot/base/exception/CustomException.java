package com.ceos21.spring_boot.base.exception;

import com.ceos21.spring_boot.base.ApiResponse;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import org.springframework.http.ResponseEntity;

public class CustomException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public CustomException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
    public ErrorStatus getErrorStatus() {
        return errorStatus;
    }
    public static <T> ResponseEntity<ApiResponse<T>> createErrorResponse(ErrorStatus errorStatus, T data) {
        return ResponseEntity
                .status(errorStatus.getHttpStatus())
                .body(ApiResponse.onFailure(errorStatus, data));
    }
}
