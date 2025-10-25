package com.yushan.gamification_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.TestcontainersConfiguration;
import com.yushan.gamification_service.dto.admin.AdminAddYuanRequestDTO;
import com.yushan.gamification_service.security.JwtAuthenticationFilter;
import com.yushan.gamification_service.service.AchievementService;
import com.yushan.gamification_service.service.GamificationService;
import com.yushan.gamification_service.service.KafkaEventProducerService;
import com.yushan.gamification_service.util.JwtTestUtil;
import com.yushan.gamification_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("integration-test")
@Transactional
@org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable(named = "CI", matches = "true")
public class AdminAndAchievementIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GamificationService gamificationService;

    @Autowired
    private AchievementService achievementService;

    @MockBean
    private JwtTestUtil jwtTestUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private KafkaEventProducerService kafkaEventProducerService;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    private MockMvc mockMvc;
    private String adminToken;
    private String userToken;
    private UUID testUserId;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", TestcontainersConfiguration.postgres::getJdbcUrl);
        registry.add("spring.datasource.username", TestcontainersConfiguration.postgres::getUsername);
        registry.add("spring.datasource.password", TestcontainersConfiguration.postgres::getPassword);

        registry.add("spring.data.redis.host", TestcontainersConfiguration.redis::getHost);
        registry.add("spring.data.redis.port", () -> TestcontainersConfiguration.redis.getMappedPort(6379));
        registry.add("jwt.secret", () -> "your-super-secret-key-for-testing-purpose-only");
        registry.add("jwt.expiration", () -> "3600000");
        registry.add("eureka.client.enabled", () -> "false");

    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        testUserId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        adminToken = jwtTestUtil.generateTestAdminToken();
        userToken = jwtTestUtil.generateTestUserToken();

    }

    @Test
    void adminAddYuan_Success() throws Exception {
        // Given
        AdminAddYuanRequestDTO request = new AdminAddYuanRequestDTO();
        request.setUserId(testUserId);
        request.setAmount(100.0);
        request.setReason("Test reward");

        // When & Then
        mockMvc.perform(post("/api/v1/gamification/admin/yuan/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        // Verify yuan was actually added
        assertEquals(100.0, gamificationService.getGamificationStatsForUser(testUserId).getYuanBalance());
    }

    @Test
    void getAdminTransactionHistory_Success() throws Exception {
        // Given
        gamificationService.adminAddYuan(testUserId, 50.0, "Initial transaction");

        // When & Then
        mockMvc.perform(get("/api/v1/gamification/admin/yuan/transactions?page=0&size=10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void achievementUnlockAndRetrieval_Success() throws Exception {
        // Given
        achievementService.checkAndUnlockLoginAchievements(testUserId);
        achievementService.checkAndUnlockCommentAchievements(testUserId, 1);

        // When & Then
        mockMvc.perform(get("/api/v1/gamification/achievements/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", not(empty())))
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(2)));
    }

    @Test
    void getGamificationStats_Success() throws Exception {
        // Given
        gamificationService.processUserLogin(testUserId);
        gamificationService.rewardComment(testUserId, 1L);

        // When & Then
        mockMvc.perform(get("/api/v1/gamification/stats/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.currentExp", greaterThan(0.0)));
    }
}
