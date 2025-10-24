package com.yushan.gamification_service.controller;

import com.yushan.gamification_service.service.KafkaEventProducerService;
import com.yushan.gamification_service.util.JwtTestUtil;
import com.yushan.gamification_service.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTestUtil jwtTestUtil;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private KafkaEventProducerService kafkaEventProducerService;

    @Test
    void getUserToken_shouldReturnUserToken() throws Exception {
        // Given
        String fakeUserToken = "fake.user.token";
        when(jwtTestUtil.generateTestUserToken()).thenReturn(fakeUserToken);

        // When & Then
        mockMvc.perform(get("/api/test/token/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("USER token generated successfully"))
                .andExpect(jsonPath("$.data.token").value(fakeUserToken))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void getAdminToken_shouldReturnAdminToken() throws Exception {
        // Given
        String fakeAdminToken = "fake.admin.token";
        when(jwtTestUtil.generateTestAdminToken()).thenReturn(fakeAdminToken);

        // When & Then
        mockMvc.perform(get("/api/test/token/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("ADMIN token generated successfully"))
                .andExpect(jsonPath("$.data.token").value(fakeAdminToken))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void getSuspendedToken_shouldReturnSuspendedToken() throws Exception {
        // Given
        String fakeSuspendedToken = "fake.suspended.token";
        when(jwtTestUtil.generateTestSuspendedToken()).thenReturn(fakeSuspendedToken);

        // When & Then
        mockMvc.perform(get("/api/test/token/suspended"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("SUSPENDED token generated successfully"))
                .andExpect(jsonPath("$.data.token").value(fakeSuspendedToken))
                .andExpect(jsonPath("$.data.role").value("USER (SUSPENDED)"));
    }
}

