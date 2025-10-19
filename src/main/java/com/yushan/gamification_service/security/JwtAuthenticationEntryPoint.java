package com.yushan.gamification_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.dto.ApiResponse;
import com.yushan.gamification_service.enums.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        logger.error("╔════════════════════════════════════════╗");
        logger.error("║   UNAUTHORIZED ACCESS - 401 ERROR     ║");
        logger.error("╚════════════════════════════════════════╝");
        logger.error("Request URI: {}", request.getRequestURI());
        logger.error("Request Method: {}", request.getMethod());
        logger.error("Authorization Header: '{}'", request.getHeader("Authorization"));
        logger.error("Authentication Exception Message: {}", authException.getMessage());
        logger.error("Exception Type: {}", authException.getClass().getName());
        logger.error("Remote Address: {}", request.getRemoteAddr());
        logger.error("════════════════════════════════════════");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Object> apiResponse = ApiResponse.error(ErrorCode.UNAUTHORIZED, "Authentication required. Please provide a valid token.");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), apiResponse);
    }
}