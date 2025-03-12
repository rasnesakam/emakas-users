package com.emakas.userService;

import com.emakas.userService.csrfTokenHandlers.SpaCsrfTokenRequestHandler;
import com.emakas.userService.handlers.UnauthorizedHandler;
import com.emakas.userService.requestFilters.AuthFilter;
import com.emakas.userService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private final UnauthorizedHandler handler;
    private final UserService userService;

    @Autowired
    public WebSecurityConfig(UnauthorizedHandler handler, UserService userService) {
        this.handler = handler;
        this.userService = userService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);
    }

    @Bean
    public AuthFilter getAuthenticationFilter(){
        return new AuthFilter();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(config -> {

            AntPathRequestMatcher authMatcher = new AntPathRequestMatcher("/api/auth/**");
            config.requireCsrfProtectionMatcher(authMatcher);
            CookieCsrfTokenRepository cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
            cookieCsrfTokenRepository.setCookieCustomizer(customizer -> {
                customizer.httpOnly(false);
                customizer.maxAge(Duration.of(10, ChronoUnit.MINUTES));
                customizer.sameSite("Strict");

            });
            config.csrfTokenRepository(cookieCsrfTokenRepository);
            config.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler());


        }).sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
