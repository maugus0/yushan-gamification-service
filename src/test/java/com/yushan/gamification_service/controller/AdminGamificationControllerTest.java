package com.yushan.gamification_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.config.SecurityConfig;
import com.yushan.gamification_service.dto.admin.AdminAddYuanRequestDTO;
import com.yushan.gamification_service.dto.common.PageResponseDTO;
import com.yushan.gamification_service.dto.transaction.AdminYuanTransactionDTO;
import com.yushan.gamification_service.security.JwtAuthenticationEntryPoint;
import com.yushan.gamification_service.service.GamificationService;
import com.yushan.gamification_service.service.KafkaEventProducerService;
import com.yushan.gamification_service.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminGamificationController.class)
@Import(SecurityConfig.class)
class AdminGamificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GamificationService gamificationService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private KafkaEventProducerService kafkaEventProducerService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final UUID testUserId = UUID.randomUUID();

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getYuanTransactions_Success() throws Exception {
        // Given
        AdminYuanTransactionDTO transaction = new AdminYuanTransactionDTO(1L, testUserId, 100.0, "Test", OffsetDateTime.now());
        PageResponseDTO<AdminYuanTransactionDTO> pageResponse = new PageResponseDTO<>(Collections.singletonList(transaction),  1, 0, 10);
        when(gamificationService.findYuanTransactionsForAdmin(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/gamification/admin/yuan/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content[0].userId").value(testUserId.toString()))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void getYuanTransactions_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/gamification/admin/yuan/transactions"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void adminAddYuan_Success() throws Exception {
        // Given
        AdminAddYuanRequestDTO request = new AdminAddYuanRequestDTO(testUserId, 100.0, "Test reward");
        doNothing().when(gamificationService).adminAddYuan(testUserId, 100.0, "Test reward");

        // When & Then
        mockMvc.perform(post("/api/v1/gamification/admin/yuan/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(String.format("Successfully added 100.00 Yuan to user %s", testUserId)));
    }

    @Test
    @WithMockUser
    void adminAddYuan_Forbidden() throws Exception {
        // Given
        AdminAddYuanRequestDTO request = new AdminAddYuanRequestDTO(testUserId, 100.0, "Test reward");

        // When & Then
        mockMvc.perform(post("/api/v1/gamification/admin/yuan/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void adminAddYuan_InvalidRequest() throws Exception {
        // Given an invalid request with a null amount
        AdminAddYuanRequestDTO request = new AdminAddYuanRequestDTO(testUserId, null, "Invalid request");

        // When & Then
        mockMvc.perform(post("/api/v1/gamification/admin/yuan/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
