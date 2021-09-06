package com.example.health.exception;

public class FileFormatNotSupportedException extends RuntimeException {

    public FileFormatNotSupportedException(String message) {
        super(message);
    }
}
