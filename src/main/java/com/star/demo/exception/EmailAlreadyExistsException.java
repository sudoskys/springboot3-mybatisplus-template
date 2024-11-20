package com.star.demo.exception;

import com.star.demo.common.ErrorCode;

import lombok.Getter;

@Getter
public class EmailAlreadyExistsException extends RuntimeException {
    private final String errorMessage;
    private final String errorCode;

    public EmailAlreadyExistsException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = ErrorCode.EMAIL_ALREADY_EXISTS.getCode();
    }
}
