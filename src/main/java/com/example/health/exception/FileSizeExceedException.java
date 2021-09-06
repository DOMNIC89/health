package com.example.health.exception;

public class FileSizeExceedException extends RuntimeException {

    public FileSizeExceedException(String message) {
        super(message);
    }
}
