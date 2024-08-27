package com.example.ormi5finalteam1.security;

import com.example.ormi5finalteam1.domain.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

//베이직 인증, api테스트용 (추후 삭제 예정입니다)
public class ProviderBasicAuthenticationFilter extends BasicAuthenticationFilter {

    public ProviderBasicAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, Authentication authResult) {
        if(authResult.getPrincipal() instanceof User u) {
            PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(
                u.toProvider(), null, u.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(token);
        }
    }
}
