package com.growwise.webhookdashboard.exception;

import com.growwise.webhookdashboard.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Catches anything unhandled in a controller and returns the same
 * ErrorResponse shape used everywhere else, instead of Spring Boot's
 * default whitelabel HTML error page.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        ErrorResponse error = new ErrorResponse(500, "Internal Server Error",
                "Something went wrong while processing the request.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
