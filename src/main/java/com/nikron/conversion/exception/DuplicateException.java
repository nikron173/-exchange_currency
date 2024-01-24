package com.nikron.conversion.exception;

public class DuplicateException extends ApplicationException {

    public DuplicateException(String message, int status) {
        super(message, status);
    }
}
