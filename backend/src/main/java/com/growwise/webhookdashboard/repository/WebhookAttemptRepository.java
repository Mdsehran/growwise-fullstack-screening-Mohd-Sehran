package com.growwise.webhookdashboard.repository;

import com.growwise.webhookdashboard.model.AttemptStatus;
import com.growwise.webhookdashboard.model.WebhookAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookAttemptRepository extends JpaRepository<WebhookAttempt, String> {

    Page<WebhookAttempt> findByStatus(AttemptStatus status, Pageable pageable);
}
