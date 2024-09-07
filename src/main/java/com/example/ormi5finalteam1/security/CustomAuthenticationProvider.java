package com.example.ormi5finalteam1.security;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @SneakyThrows
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        try {
            User user = (User) userDetailsService.loadUserByUsername(email);

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (BusinessException e) {
            log.error("Business exception during authentication: ", e);
            throw new AuthenticationServiceException(e.getMessage(), e);
        } catch (BadCredentialsException e) {
            log.error("Bad credentials exception during authentication: ", e);
            throw new AuthenticationServiceException("Invalid credentials", e);
        } catch (Exception e) {
            log.error("Unexpected error during authentication: ", e);
            throw new AuthenticationServiceException("Authentication failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

