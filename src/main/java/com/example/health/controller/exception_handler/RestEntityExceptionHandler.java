package com.example.health.controller.exception_handler;

import com.example.health.co.ApiError;
import com.example.health.exception.CategoryNotFoundException;
import com.example.health.exception.FileFormatNotSupportedException;
import com.example.health.exception.FileSizeExceedException;
import com.example.health.exception.NoVideosFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final HttpHeaders EMPTY_HEADERS = new HttpHeaders();

    @ExceptionHandler(value = {FileFormatNotSupportedException.class})
    protected ResponseEntity<Object> handleFileFormatNotSupportedException(FileFormatNotSupportedException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage()), EMPTY_HEADERS,
                HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = {CategoryNotFoundException.class})
    protected ResponseEntity<Object> handleCategoryNotFoundException(CategoryNotFoundException e, WebRequest request) {
        return handleExceptionInternal(e, new ApiError(HttpStatus.NOT_FOUND, e.getMessage()), EMPTY_HEADERS, HttpStatus.NOT_FOUND,
                request);
    }

    @ExceptionHandler(value = {FileSizeExceedException.class})
    protected ResponseEntity<Object> handleFileSizeExceedException(FileSizeExceedException e, WebRequest request) {
        return handleExceptionInternal(e, new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()), EMPTY_HEADERS,
                HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(value = {NoVideosFoundException.class})
    protected ResponseEntity<Object> handleNoVideosFoundException(NoVideosFoundException e, WebRequest request) {
        return handleExceptionInternal(e, new ApiError(HttpStatus.NOT_FOUND, e.getMessage()), EMPTY_HEADERS,
                HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {FileNotFoundException.class})
    protected ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException e, WebRequest request) {
        return handleExceptionInternal(e, new ApiError(HttpStatus.NOT_FOUND, e.getMessage()), EMPTY_HEADERS,
                HttpStatus.NOT_FOUND, request);
    }
}
