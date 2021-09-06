package com.example.health.exception;

public class NoVideosFoundException extends RuntimeException {
    public NoVideosFoundException(String message) {
        super(message);
    }
}
