package com.growwise.webhookdashboard.seed;

import com.growwise.webhookdashboard.model.AttemptStatus;
import com.growwise.webhookdashboard.model.Role;
import com.growwise.webhookdashboard.model.User;
import com.growwise.webhookdashboard.model.WebhookAttempt;
import com.growwise.webhookdashboard.model.WebhookSubscription;
import com.growwise.webhookdashboard.repository.UserRepository;
import com.growwise.webhookdashboard.repository.WebhookAttemptRepository;
import com.growwise.webhookdashboard.repository.WebhookSubscriptionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Seeds deterministic demo data on every startup (the H2 database is
 * in-memory and recreated each run via ddl-auto=create-drop, so this is
 * safe and idempotent in practice).
 *
 * IDs are fixed string constants rather than randomly generated UUIDs so
 * they can be documented verbatim in the README and pasted directly into
 * curl examples / the frontend's auth switcher without first inspecting
 * the database.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    public static final String ADMIN_ID = "11111111-1111-1111-1111-111111111111";
    public static final String INSTRUCTOR_ID = "22222222-2222-2222-2222-222222222222";
    public static final String STUDENT_ID = "33333333-3333-3333-3333-333333333333";

    public static final String SUBSCRIPTION_1_ID = "sub-aaaa1111";
    public static final String SUBSCRIPTION_2_ID = "sub-bbbb2222";

    private final UserRepository userRepository;
    private final WebhookSubscriptionRepository subscriptionRepository;
    private final WebhookAttemptRepository attemptRepository;

    public DataSeeder(UserRepository userRepository,
                       WebhookSubscriptionRepository subscriptionRepository,
                       WebhookAttemptRepository attemptRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.attemptRepository = attemptRepository;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedSubscriptions();
        seedAttempts();
    }

    private void seedUsers() {
        userRepository.save(new User(ADMIN_ID, "Ava Admin", "ava.admin@growwise.dev", Role.ADMIN));
        userRepository.save(new User(INSTRUCTOR_ID, "Ian Instructor", "ian.instructor@growwise.dev", Role.INSTRUCTOR));
        userRepository.save(new User(STUDENT_ID, "Sam Student", "sam.student@growwise.dev", Role.STUDENT));
    }

    private void seedSubscriptions() {
        Instant now = Instant.now();
        subscriptionRepository.save(new WebhookSubscription(
                SUBSCRIPTION_1_ID, "LMS Question Events", "https://example-lms.invalid/webhooks/questions", now));
        subscriptionRepository.save(new WebhookSubscription(
                SUBSCRIPTION_2_ID, "Grading Pipeline Events", "https://example-grading.invalid/webhooks/grades", now));
    }

    private void seedAttempts() {
        Instant now = Instant.now();

        // 3 SUCCESS, 3 FAILED, 2 PENDING = 8 total, matching the brief's
        // "mixed distribution" requirement exactly.
        save(att("att-0001", SUBSCRIPTION_1_ID, "QUESTION_ASKED", AttemptStatus.SUCCESS, 200,
                now.minus(6, ChronoUnit.HOURS), null));
        save(att("att-0002", SUBSCRIPTION_1_ID, "QUESTION_ANSWERED", AttemptStatus.SUCCESS, 200,
                now.minus(5, ChronoUnit.HOURS), null));
        save(att("att-0003", SUBSCRIPTION_2_ID, "GRADE_PUBLISHED", AttemptStatus.SUCCESS, 201,
                now.minus(4, ChronoUnit.HOURS), null));

        save(att("att-0004", SUBSCRIPTION_1_ID, "QUESTION_ASKED", AttemptStatus.FAILED, 500,
                now.minus(3, ChronoUnit.HOURS), "Connection timeout"));
        save(att("att-0005", SUBSCRIPTION_2_ID, "GRADE_PUBLISHED", AttemptStatus.FAILED, 503,
                now.minus(2, ChronoUnit.HOURS).minus(30, ChronoUnit.MINUTES), "Service unavailable"));
        save(att("att-0006", SUBSCRIPTION_1_ID, "QUESTION_ANSWERED", AttemptStatus.FAILED, 404,
                now.minus(2, ChronoUnit.HOURS), "Target endpoint not found"));

        save(att("att-0007", SUBSCRIPTION_2_ID, "GRADE_PUBLISHED", AttemptStatus.PENDING, null,
                now.minus(30, ChronoUnit.MINUTES), null));
        save(att("att-0008", SUBSCRIPTION_1_ID, "QUESTION_ASKED", AttemptStatus.PENDING, null,
                now.minus(10, ChronoUnit.MINUTES), null));
    }

    private WebhookAttempt att(String id, String subscriptionId, String eventType, AttemptStatus status,
                                Integer responseCode, Instant createdAt, String lastError) {
        WebhookAttempt attempt = new WebhookAttempt();
        attempt.setId(id);
        attempt.setSubscriptionId(subscriptionId);
        attempt.setEventType(eventType);
        attempt.setStatus(status);
        attempt.setResponseCode(responseCode);
        attempt.setCreatedAt(createdAt);
        attempt.setLastError(lastError);
        return attempt;
    }

    private void save(WebhookAttempt attempt) {
        attemptRepository.save(attempt);
    }
}
