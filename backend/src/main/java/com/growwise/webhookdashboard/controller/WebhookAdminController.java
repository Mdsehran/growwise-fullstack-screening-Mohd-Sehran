package com.growwise.webhookdashboard.controller;

import com.growwise.webhookdashboard.dto.ErrorResponse;
import com.growwise.webhookdashboard.dto.PagedResponse;
import com.growwise.webhookdashboard.dto.WebhookAttemptResponse;
import com.growwise.webhookdashboard.model.AttemptStatus;
import com.growwise.webhookdashboard.model.WebhookAttempt;
import com.growwise.webhookdashboard.repository.WebhookAttemptRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Every endpoint here sits behind /api/admin/** and is therefore already
 * gated by AdminAuthInterceptor — by the time a request body reaches this
 * class, X-Role: ADMIN has already been verified. No role checks are
 * duplicated here; that would be redundant and is exactly the kind of
 * scattered, easy-to-forget authorization logic the interceptor pattern
 * avoids.
 */
@RestController
@RequestMapping("/api/admin/webhook-attempts")
public class WebhookAdminController {

    private final WebhookAttemptRepository attemptRepository;

    public WebhookAdminController(WebhookAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @GetMapping
    public ResponseEntity<?> listAttempts(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        Page<WebhookAttempt> result;
        if (status != null && !status.isBlank()) {
            AttemptStatus parsedStatus;
            try {
                parsedStatus = AttemptStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ex) {
                ErrorResponse error = new ErrorResponse(400, "Bad Request",
                        "Invalid status '" + status + "'. Expected one of SUCCESS, FAILED, PENDING.");
                return ResponseEntity.badRequest().body(error);
            }
            result = attemptRepository.findByStatus(parsedStatus, pageable);
        } else {
            result = attemptRepository.findAll(pageable);
        }

        List<WebhookAttemptResponse> content = result.getContent().stream()
                .map(WebhookAttemptResponse::fromEntity)
                .toList();

        PagedResponse<WebhookAttemptResponse> response = new PagedResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{attemptId}/retry")
    public ResponseEntity<?> retryAttempt(@PathVariable String attemptId) {
        WebhookAttempt original = attemptRepository.findById(attemptId).orElse(null);

        if (original == null) {
            ErrorResponse error = new ErrorResponse(404, "Not Found",
                    "No webhook attempt found with id '" + attemptId + "'.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        if (original.getStatus() != AttemptStatus.FAILED) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request",
                    "Attempt '" + attemptId + "' has status " + original.getStatus()
                            + " and cannot be retried. Only FAILED attempts are eligible for retry.");
            return ResponseEntity.badRequest().body(error);
        }

        // Mocked retry: the original FAILED record is left completely
        // untouched (it stays in the log exactly as it happened) and a
        // brand-new PENDING record is appended to simulate handing the
        // delivery back to an async queue. No real outbound HTTP call is
        // made anywhere in this codebase.
        WebhookAttempt retryRecord = new WebhookAttempt(
                UUID.randomUUID().toString(),
                original.getSubscriptionId(),
                original.getEventType(),
                AttemptStatus.PENDING,
                null,
                Instant.now(),
                null,
                original.getId()
        );

        WebhookAttempt saved = attemptRepository.save(retryRecord);

        return ResponseEntity.status(HttpStatus.CREATED).body(WebhookAttemptResponse.fromEntity(saved));
    }
}
