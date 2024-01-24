package com.nikron.conversion.exception;

public class NotFoundException extends ApplicationException {
    public NotFoundException(String message, int status) {
        super(message, status);
    }
}
