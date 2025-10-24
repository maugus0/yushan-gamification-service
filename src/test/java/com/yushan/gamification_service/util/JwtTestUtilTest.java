package com.yushan.gamification_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTestUtilTest {

    @InjectMocks
    private JwtTestUtil jwtTestUtil;

    private final String testSecret = "a-very-secure-and-long-secret-key-for-testing-purposes-12345";
    private final String testIssuer = "test-issuer";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTestUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtTestUtil, "issuer", testIssuer);
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Test
    void generateTestUserToken_ShouldGenerateValidTokenWithUserClaims() {
        // When
        String token = jwtTestUtil.generateTestUserToken();

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = parseToken(token);
        assertEquals("550e8400-e29b-41d4-a716-446655440000", claims.get("userId"));
        assertEquals("user@test.com", claims.get("email"));
        assertEquals("test_user", claims.get("username"));
        assertEquals("USER", claims.get("role"));
        assertEquals(0, claims.get("status", Integer.class));
        assertEquals("access", claims.get("tokenType"));
        assertEquals(testIssuer, claims.getIssuer());
    }

    @Test
    void generateTestAuthorToken_ShouldGenerateValidTokenWithAuthorClaims() {
        // When
        String token = jwtTestUtil.generateTestAuthorToken();

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = parseToken(token);
        assertEquals("550e8400-e29b-41d4-a716-446655440001", claims.get("userId"));
        assertEquals("author@test.com", claims.get("email"));
        assertEquals("test_author", claims.get("username"));
        assertEquals("AUTHOR", claims.get("role"));
        assertEquals(0, claims.get("status", Integer.class));
        assertEquals(testIssuer, claims.getIssuer());
    }

    @Test
    void generateTestAdminToken_ShouldGenerateValidTokenWithAdminClaims() {
        // When
        String token = jwtTestUtil.generateTestAdminToken();

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = parseToken(token);
        assertEquals("550e8400-e29b-41d4-a716-446655440002", claims.get("userId"));
        assertEquals("admin@test.com", claims.get("email"));
        assertEquals("test_admin", claims.get("username"));
        assertEquals("ADMIN", claims.get("role"));
        assertEquals(0, claims.get("status", Integer.class));
        assertEquals(testIssuer, claims.getIssuer());
    }

    @Test
    void generateTestSuspendedToken_ShouldGenerateValidTokenWithSuspendedStatus() {
        // When
        String token = jwtTestUtil.generateTestSuspendedToken();

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = parseToken(token);
        assertEquals("550e8400-e29b-41d4-a716-446655440003", claims.get("userId"));
        assertEquals("suspended@test.com", claims.get("email"));
        assertEquals("test_suspended", claims.get("username"));
        assertEquals("AUTHOR", claims.get("role"));
        assertEquals(1, claims.get("status", Integer.class)); // Verify suspended status
        assertEquals(testIssuer, claims.getIssuer());
    }
}
