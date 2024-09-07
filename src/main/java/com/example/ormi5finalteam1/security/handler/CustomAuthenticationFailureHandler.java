package com.example.ormi5finalteam1.security.handler;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        String errorType = "INVALID_CREDENTIALS";
        String errorMessage = "이메일 혹은 비밀번호가 일치하지 않습니다.";

        if (exception instanceof AuthenticationServiceException) {
            Throwable cause = exception.getCause();
            if (cause instanceof BusinessException businessException) {
                ErrorCode errorCode = businessException.getErrorCode();
                errorType = getErrorType(errorCode);
                errorMessage = getErrorMessage(errorCode);
            } else if ("Invalid credentials".equals(exception.getMessage())) {
                errorType = "INVALID_CREDENTIALS";
                errorMessage = "이메일 혹은 비밀번호가 일치하지 않습니다.";
            } else {
                errorType = "AUTHENTICATION_FAILED";
                errorMessage = "인증 중 오류가 발생했습니다.";
            }
        }

        String redirectUrl = String.format("/login?error=%s&message=%s",
            URLEncoder.encode(errorType, StandardCharsets.UTF_8),
            URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));

        response.sendRedirect(redirectUrl);
    }

    private String getErrorType(ErrorCode errorCode) {
        return switch (errorCode) {
            case USER_DEACTIVATED -> "DEACTIVATED";
            case USER_SUSPENDED -> "SUSPENDED";
            case USER_NOT_FOUND -> "INVALID_CREDENTIALS";
            default -> "AUTHENTICATION_FAILED";
        };
    }

    private String getErrorMessage(ErrorCode errorCode) {
        return switch (errorCode) {
            case USER_DEACTIVATED -> "탈퇴한 계정입니다.";
            case USER_SUSPENDED -> "정지된 계정입니다.";
            case USER_NOT_FOUND -> "이메일 혹은 비밀번호가 일치하지 않습니다.";
            default -> "인증 중 오류가 발생했습니다.";
        };
    }
}