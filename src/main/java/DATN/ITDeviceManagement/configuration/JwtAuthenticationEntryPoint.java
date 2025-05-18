package DATN.ITDeviceManagement.configuration;


import DATN.ITDeviceManagement.DTO.response.ApiResponse;
import DATN.ITDeviceManagement.exception.ErrorCode;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;


public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, AuthenticationException authException) throws IOException, javax.servlet.ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .message(errorCode.getMessage())
                .success(false)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}

