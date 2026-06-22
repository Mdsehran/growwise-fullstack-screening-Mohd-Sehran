package com.growwise.webhookdashboard.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.Instant;

/**
 * Persistence model for a single webhook delivery attempt.
 *
 * NOTE on `retriedFromAttemptId`: the public API contract (see
 * dto.WebhookAttemptResponse) exposes EXACTLY the seven fields specified in
 * the brief (id, subscriptionId, eventType, status, responseCode,
 * createdAt, lastError) — nothing more. This column is an internal-only
 * audit/lineage marker so a retry's new PENDING row can be traced back to
 * the FAILED row that spawned it, without mutating the original log entry.
 * It is deliberately excluded from API responses by using a DTO mapper
 * rather than serializing the entity directly. See README "Trade-offs".
 */
@Entity
@Table(name = "webhook_attempt")
public class WebhookAttempt {

    @Id
    private String id;

    @Column(nullable = false)
    private String subscriptionId;

    @Column(nullable = false)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status;

    private Integer responseCode;

    @Column(nullable = false)
    private Instant createdAt;

    private String lastError;

    private String retriedFromAttemptId;

    public WebhookAttempt() {
    }

    public WebhookAttempt(String id, String subscriptionId, String eventType, AttemptStatus status,
                           Integer responseCode, Instant createdAt, String lastError,
                           String retriedFromAttemptId) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.eventType = eventType;
        this.status = status;
        this.responseCode = responseCode;
        this.createdAt = createdAt;
        this.lastError = lastError;
        this.retriedFromAttemptId = retriedFromAttemptId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getRetriedFromAttemptId() {
        return retriedFromAttemptId;
    }

    public void setRetriedFromAttemptId(String retriedFromAttemptId) {
        this.retriedFromAttemptId = retriedFromAttemptId;
    }
}
