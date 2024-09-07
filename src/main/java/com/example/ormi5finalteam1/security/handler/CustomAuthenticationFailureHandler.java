package com.example.ormi5finalteam1.security.handler;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.common.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DEFAULT_ERROR_MESSAGE = "이메일 혹은 비밀번호가 일치하지 않습니다.";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        ErrorResponse errorResponse;

        if (exception instanceof AuthenticationServiceException
            && exception.getCause() instanceof BusinessException businessException) {
            errorResponse = ErrorResponse.of(businessException.getErrorCode(),
                translateErrorMessage(businessException.getErrorCode()));
        } else {
            errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED, DEFAULT_ERROR_MESSAGE);
        }

        response.setStatus(errorResponse.status());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private String translateErrorMessage(ErrorCode errorCode) {
        return switch (errorCode) {
            case USER_NOT_FOUND -> DEFAULT_ERROR_MESSAGE;
            case USER_DEACTIVATED -> "비활성화된 사용자입니다.";
            case USER_SUSPENDED -> "정지된 사용자입니다.";
            default -> errorCode.getMessage();
        };
    }
}