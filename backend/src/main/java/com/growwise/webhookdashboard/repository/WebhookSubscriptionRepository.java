package com.growwise.webhookdashboard.repository;

import com.growwise.webhookdashboard.model.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, String> {
}
