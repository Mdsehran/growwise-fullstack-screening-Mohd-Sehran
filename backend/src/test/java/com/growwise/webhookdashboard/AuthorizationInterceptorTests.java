package com.growwise.webhookdashboard;

import com.growwise.webhookdashboard.seed.DataSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Auto-fail safeguard: proves authorization is enforced SERVER-SIDE, not
 * just hidden in the frontend. These tests hit the controller directly via
 * MockMvc with no frontend involved at all.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationInterceptorTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void studentRoleIsForbiddenFromAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/webhook-attempts")
                        .header("X-User-Id", DataSeeder.STUDENT_ID)
                        .header("X-Role", "STUDENT"))
                .andExpect(status().isForbidden());
    }

    @Test
    void instructorRoleIsForbiddenFromAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/webhook-attempts")
                        .header("X-User-Id", DataSeeder.INSTRUCTOR_ID)
                        .header("X-Role", "INSTRUCTOR"))
                .andExpect(status().isForbidden());
    }

    @Test
    void missingHeadersAreForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/webhook-attempts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminRoleIsAllowedThroughToAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/webhook-attempts")
                        .header("X-User-Id", DataSeeder.ADMIN_ID)
                        .header("X-Role", "ADMIN"))
                .andExpect(status().isOk());
    }
}
