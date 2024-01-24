package com.nikron.conversion.exception;

public class BadRequestException extends ApplicationException {
    public BadRequestException(String message, int status) {
        super(message, status);
    }
}
