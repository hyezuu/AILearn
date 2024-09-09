package com.example.ormi5finalteam1.security;

import com.example.ormi5finalteam1.security.CustomAuthenticationProvider;
import com.example.ormi5finalteam1.security.ProviderBasicAuthenticationFilter;
import com.example.ormi5finalteam1.security.handler.CustomAuthenticationFailureHandler;
import com.example.ormi5finalteam1.security.handler.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationProvider provider;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
        throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable).cors(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(api -> api
                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                .requestMatchers("/signup", "/login", "/", "/forgot-password").permitAll()
                .requestMatchers("/api/nickname-duplication","/api/email-duplication").permitAll()
                .requestMatchers("/api/request-verification", "/api/verify-email", "/api/auth/password").permitAll()
                .requestMatchers("/*/signup","/*/login").permitAll()
                .requestMatchers("/api/me").hasRole("USER")
                .requestMatchers("/my").hasRole("USER")
                    .requestMatchers("/tests", "/tests/level-tests", "/level-tests", "/test-result").authenticated()
                .anyRequest().hasAnyRole("USER","ADMIN"))
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .permitAll()
            )
            .logout(logout -> logout.logoutUrl("/logout"))
            .authenticationProvider(provider)
//            .requiresChannel(channel -> channel.anyRequest().requiresSecure()) // HTTPS 리다이렉트 설정 추가
            .with(new MyCustomDsl(), myCustomDsl -> {
                try {
                    myCustomDsl.init(http);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            })
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    //todo : 에러페이지 redirect
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("접근 권한이 없습니다.");
                })
            ).build();

    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    public static class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) {
            AuthenticationManager authenticationManager = http.getSharedObject(
                AuthenticationManager.class);
            http
                .addFilterBefore(
                    new ProviderBasicAuthenticationFilter(authenticationManager),
                    BasicAuthenticationFilter.class);
        }
    }
}
