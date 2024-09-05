package com.example.ormi5finalteam1.security.handler;

import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (authentication.getPrincipal() instanceof User u) {
            Provider provider = u.toProvider();
            PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(
                    provider, null, u.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(token);

            response.sendRedirect(provider.grade() == null ? "/tests" : "/");
        } else {
            response.sendRedirect("/");
        }
    }
}