package com.yushan.gamification_service.security;

import com.yushan.gamification_service.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        logger.info("========================================");
        logger.info("=== JWT Filter Processing START ===");
        logger.info("Request URI: {}", requestURI);
        logger.info("Request Method: {}", method);
        logger.info("========================================");

        try {
            String jwt = getJwtFromRequest(request);

            if (jwt != null) {
                logger.info("✓ JWT Token found in request");
                logger.info("Token (first 30 chars): {}...", jwt.substring(0, Math.min(jwt.length(), 30)));
            } else {
                logger.warn("✗ No JWT Token found in request");
            }

            if (StringUtils.hasText(jwt)) {
                logger.info("Attempting to validate token...");

                boolean isValid = jwtUtil.validateToken(jwt);
                logger.info("Token validation result: {}", isValid ? "VALID ✓" : "INVALID ✗");

                if (isValid) {
                    logger.info("Extracting user ID from token...");
                    UUID userId = jwtUtil.getUserIdFromToken(jwt);
                    logger.info("✓ User ID extracted: {}", userId);

                    UserDetails userDetails = new User(userId.toString(), "", Collections.emptyList());
                    logger.info("✓ UserDetails created: {}", userDetails.getUsername());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.info("✓✓✓ Authentication SUCCESS! User {} is now authenticated", userId);
                    logger.info("SecurityContext authentication: {}", SecurityContextHolder.getContext().getAuthentication());
                } else {
                    logger.error("✗✗✗ Token validation FAILED - User will be UNAUTHORIZED");
                }
            } else {
                logger.warn("No token text to validate - skipping authentication");
            }
        } catch (Exception ex) {
            logger.error("✗✗✗ EXCEPTION during authentication process", ex);
            logger.error("Exception type: {}", ex.getClass().getName());
            logger.error("Exception message: {}", ex.getMessage());
        }

        logger.info("========================================");
        logger.info("=== JWT Filter Processing END ===");
        logger.info("Current Authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        logger.info("========================================");

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        logger.info("Authorization Header Value: '{}'", bearerToken);

        if (StringUtils.hasText(bearerToken)) {
            if (bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);
                logger.info("✓ Bearer token extracted successfully");
                logger.info("Extracted token length: {}", token.length());
                return token;
            } else {
                logger.warn("✗ Authorization header does NOT start with 'Bearer '");
                logger.warn("Header starts with: '{}'", bearerToken.substring(0, Math.min(bearerToken.length(), 10)));
            }
        } else {
            logger.warn("✗ Authorization header is empty or null");
        }

        return null;
    }
}