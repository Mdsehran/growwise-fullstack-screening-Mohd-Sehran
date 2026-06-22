package com.growwise.webhookdashboard;

import com.growwise.webhookdashboard.model.AttemptStatus;
import com.growwise.webhookdashboard.model.WebhookAttempt;
import com.growwise.webhookdashboard.repository.WebhookAttemptRepository;
import com.growwise.webhookdashboard.seed.DataSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Covers the retry lifecycle end-to-end through MockMvc (not by calling the
 * controller method directly), and the validation boundary that blocks
 * retrying anything that isn't currently FAILED.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RetryWorkflowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebhookAttemptRepository attemptRepository;

    private static final String FAILED_ATTEMPT_ID = "test-failed-attempt";
    private static final String SUCCESS_ATTEMPT_ID = "test-success-attempt";

    @BeforeEach
    void setUp() {
        WebhookAttempt failed = new WebhookAttempt();
        failed.setId(FAILED_ATTEMPT_ID);
        failed.setSubscriptionId(DataSeeder.SUBSCRIPTION_1_ID);
        failed.setEventType("QUESTION_ASKED");
        failed.setStatus(AttemptStatus.FAILED);
        failed.setResponseCode(500);
        failed.setCreatedAt(Instant.now());
        failed.setLastError("Connection timeout");
        attemptRepository.save(failed);

        WebhookAttempt success = new WebhookAttempt();
        success.setId(SUCCESS_ATTEMPT_ID);
        success.setSubscriptionId(DataSeeder.SUBSCRIPTION_1_ID);
        success.setEventType("QUESTION_ASKED");
        success.setStatus(AttemptStatus.SUCCESS);
        success.setResponseCode(200);
        success.setCreatedAt(Instant.now());
        attemptRepository.save(success);
    }

    @Test
    void retryingAFailedAttemptCreatesNewPendingRecordAndLeavesOriginalUntouched() throws Exception {
        long countBefore = attemptRepository.count();

        mockMvc.perform(post("/api/admin/webhook-attempts/{id}/retry", FAILED_ATTEMPT_ID)
                        .header("X-User-Id", DataSeeder.ADMIN_ID)
                        .header("X-Role", "ADMIN"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.subscriptionId").value(DataSeeder.SUBSCRIPTION_1_ID));

        assertThat(attemptRepository.count()).isEqualTo(countBefore + 1);

        WebhookAttempt originalStillFailed = attemptRepository.findById(FAILED_ATTEMPT_ID).orElseThrow();
        assertThat(originalStillFailed.getStatus()).isEqualTo(AttemptStatus.FAILED);

        List<WebhookAttempt> pendingRetries = attemptRepository.findAll().stream()
                .filter(a -> FAILED_ATTEMPT_ID.equals(a.getRetriedFromAttemptId()))
                .toList();
        assertThat(pendingRetries).hasSize(1);
        assertThat(pendingRetries.get(0).getStatus()).isEqualTo(AttemptStatus.PENDING);
    }

    @Test
    void retryingASuccessfulAttemptIsRejectedWithBadRequest() throws Exception {
        mockMvc.perform(post("/api/admin/webhook-attempts/{id}/retry", SUCCESS_ATTEMPT_ID)
                        .header("X-User-Id", DataSeeder.ADMIN_ID)
                        .header("X-Role", "ADMIN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void retryingAnUnknownAttemptReturnsNotFound() throws Exception {
        mockMvc.perform(post("/api/admin/webhook-attempts/{id}/retry", "does-not-exist")
                        .header("X-User-Id", DataSeeder.ADMIN_ID)
                        .header("X-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }
}
