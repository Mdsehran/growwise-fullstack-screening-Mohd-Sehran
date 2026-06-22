package com.growwise.webhookdashboard.model;

/**
 * The three roles recognized by the simulated header-based auth scheme.
 * There is intentionally no "anonymous"/unauthenticated state here — a
 * missing or unrecognized X-Role header is rejected by the interceptor
 * before it ever reaches application code.
 */
public enum Role {
    ADMIN,
    INSTRUCTOR,
    STUDENT
}
