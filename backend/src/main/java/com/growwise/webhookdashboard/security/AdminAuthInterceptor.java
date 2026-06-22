package com.growwise.webhookdashboard.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.growwise.webhookdashboard.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_ROLE = "X-Role";
    private static final String REQUIRED_ROLE = "ADMIN";

    private final ObjectMapper objectMapper;

    // Injects the same ObjectMapper bean Spring Boot already configured
    // with the Java 8 date/time (JavaTimeModule) support — instead of a
    // bare `new ObjectMapper()`, which can't serialize Instant and was
    // throwing under the hood on every 403 response.
    public AdminAuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String userId = request.getHeader(HEADER_USER_ID);
        String role = request.getHeader(HEADER_ROLE);

        boolean hasUserId = userId != null && !userId.isBlank();
        boolean hasAdminRole = REQUIRED_ROLE.equals(role);

        if (!hasUserId || !hasAdminRole) {
            rejectAsForbidden(response, role);
            return false;
        }

        return true;
    }

    private void rejectAsForbidden(HttpServletResponse response, String role) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String message = (role == null || role.isBlank())
                ? "Missing or invalid X-Role header. Only ADMIN may access this resource."
                : "Role '" + role + "' is not permitted to access this resource. ADMIN required.";

        ErrorResponse body = new ErrorResponse(403, "Forbidden", message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}