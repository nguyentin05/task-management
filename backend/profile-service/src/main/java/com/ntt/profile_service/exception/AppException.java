package com.ntt.profile_service.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final com.ntt.profile_service.exception.ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}