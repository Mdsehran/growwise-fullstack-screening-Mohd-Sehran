package com.growwise.webhookdashboard.dto;

import com.growwise.webhookdashboard.model.AttemptStatus;
import com.growwise.webhookdashboard.model.WebhookAttempt;
import java.time.Instant;

/**
 * The exact public API shape for a webhook attempt, matching the brief's
 * schema field-for-field: id, subscriptionId, eventType, status,
 * responseCode, createdAt, lastError. Internal-only entity fields (such as
 * WebhookAttempt#retriedFromAttemptId) are never exposed here.
 */
public class WebhookAttemptResponse {

    private final String id;
    private final String subscriptionId;
    private final String eventType;
    private final AttemptStatus status;
    private final Integer responseCode;
    private final Instant createdAt;
    private final String lastError;

    public WebhookAttemptResponse(String id, String subscriptionId, String eventType, AttemptStatus status,
                                   Integer responseCode, Instant createdAt, String lastError) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.eventType = eventType;
        this.status = status;
        this.responseCode = responseCode;
        this.createdAt = createdAt;
        this.lastError = lastError;
    }

    public static WebhookAttemptResponse fromEntity(WebhookAttempt attempt) {
        return new WebhookAttemptResponse(
                attempt.getId(),
                attempt.getSubscriptionId(),
                attempt.getEventType(),
                attempt.getStatus(),
                attempt.getResponseCode(),
                attempt.getCreatedAt(),
                attempt.getLastError()
        );
    }

    public String getId() {
        return id;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getEventType() {
        return eventType;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getLastError() {
        return lastError;
    }
}
