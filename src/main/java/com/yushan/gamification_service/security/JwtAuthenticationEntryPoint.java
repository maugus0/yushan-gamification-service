package com.yushan.gamification_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yushan.gamification_service.dto.ApiResponse;
import com.yushan.gamification_service.enums.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Object> apiResponse = ApiResponse.error(ErrorCode.UNAUTHORIZED, "Authentication required. Please provide a valid token.");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
