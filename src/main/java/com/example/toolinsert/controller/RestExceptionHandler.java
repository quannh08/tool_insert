package com.example.toolinsert.controller;

import com.example.toolinsert.model.ApiErrorResponse;
import java.time.Instant;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", exception.getMessage())
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingPart(MissingServletRequestPartException exception) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", exception.getMessage())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        "Conflict",
                        "Import data violates a database uniqueness rule."
                )
        );
    }
}
