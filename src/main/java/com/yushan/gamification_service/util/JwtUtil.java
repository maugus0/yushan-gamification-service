// 最终文件路径: com/yushan/gamification_service/util/JwtUtil.java

package com.yushan.gamification_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public UUID getUserIdFromToken(String token) {
        String id = getClaimsFromToken(token).getSubject();
        return UUID.fromString(id);
    }

    public boolean validateToken(String authToken) {
        logger.info(">>> Validating JWT Token...");
        logger.info("Token length: {}", authToken.length());
        logger.info("Token preview: {}...", authToken.substring(0, Math.min(authToken.length(), 50)));

        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            logger.info(">>> Token validation: SUCCESS ✓✓✓");
            return true;
        } catch (MalformedJwtException ex) {
            logger.error(">>> Token validation FAILED: Invalid JWT token");
            logger.error(">>> Error details: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error(">>> Token validation FAILED: Expired JWT token");
            logger.error(">>> Token expired at: {}", ex.getClaims().getExpiration());
            logger.error(">>> Current time: {}", new java.util.Date());
        } catch (UnsupportedJwtException ex) {
            logger.error(">>> Token validation FAILED: Unsupported JWT token");
            logger.error(">>> Error details: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error(">>> Token validation FAILED: JWT claims string is empty");
            logger.error(">>> Error details: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error(">>> Token validation FAILED: Unknown error");
            logger.error(">>> Exception type: {}", ex.getClass().getName());
            logger.error(">>> Error message: {}", ex.getMessage());
            logger.error(">>> Stack trace:", ex);
        }
        return false;
    }


    public String generateToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSigningKey())
                .compact();
    }
}