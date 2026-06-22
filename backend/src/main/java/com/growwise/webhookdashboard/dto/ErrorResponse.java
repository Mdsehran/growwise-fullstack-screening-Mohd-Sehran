package com.growwise.webhookdashboard.dto;

import java.time.Instant;

/**
 * Uniform error payload used for every non-2xx response in this service
 * (403 from the auth interceptor, 400/404 from the controller, 500 from
 * the global exception handler) so the frontend has exactly one shape to
 * parse for error states.
 */
public class ErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final Instant timestamp;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
