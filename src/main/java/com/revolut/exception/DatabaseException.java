package com.revolut.exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message, Exception cause) {
        super(message, cause);
    }
}
