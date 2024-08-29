package com.example.ormi5finalteam1.util;

import com.example.ormi5finalteam1.domain.user.Provider;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class TestSecurityContextFactory {

    public static RequestPostProcessor authenticatedProvider(Provider provider) {
        return request -> {
            SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(provider, null, "ROLE_USER"));
            return request;
        };
    }

}
