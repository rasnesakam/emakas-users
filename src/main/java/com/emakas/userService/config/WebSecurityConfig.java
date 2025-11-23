package com.emakas.userService.config;

import com.emakas.userService.csrfTokenHandlers.SpaCsrfTokenRequestHandler;
import com.emakas.userService.handlers.UnauthorizedHandler;
import com.emakas.userService.requestFilters.AuthFilter;
import com.emakas.userService.service.ClientCredentialsService;
import com.emakas.userService.service.UserService;
import com.emakas.userService.shared.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final UnauthorizedHandler handler;
    private final UserService userService;
    private final TokenManager tokenManager;


    @Autowired
    public WebSecurityConfig(UnauthorizedHandler handler, UserService userService, TokenManager tokenManager) {
        this.handler = handler;
        this.userService = userService;
        this.tokenManager = tokenManager;
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
    public AuthFilter getAuthFilter(ClientCredentialsService clientCredentialsService) {
        return new AuthFilter(tokenManager, clientCredentialsService);
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(config -> {
            AntPathRequestMatcher authPostMatcher = new AntPathRequestMatcher("/api/auth/**", HttpMethod.POST.name());
            AntPathRequestMatcher authPutMatcher = new AntPathRequestMatcher("/api/auth/**", HttpMethod.PUT.name());
            AntPathRequestMatcher authDeleteMatcher = new AntPathRequestMatcher("/api/auth/**", HttpMethod.DELETE.name());
            OrRequestMatcher orRequestMatcher = new OrRequestMatcher(List.of(authPostMatcher, authPutMatcher, authDeleteMatcher));
            config.requireCsrfProtectionMatcher(orRequestMatcher);
            CookieCsrfTokenRepository cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
            cookieCsrfTokenRepository.setCookieCustomizer(customizer -> {
                customizer.httpOnly(false);
                customizer.maxAge(Duration.of(10, ChronoUnit.MINUTES));
                customizer.sameSite("Strict");

            });
            config.csrfTokenRepository(cookieCsrfTokenRepository);
            config.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler());

        }).sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //TODO: Replace our auth filter with default one
        /*
        http.addFilterAt(getAuthFilter(), AuthorizationFilter.class)
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll()
                );
         */
        return http.build();
    }

    /*
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(TokenPermissionEvaluator permissionEvaluator) {
        CustomMethodSecurityExpressionHandler customMethodSecurityExpressionHandler = new CustomMethodSecurityExpressionHandler();
        customMethodSecurityExpressionHandler.setPermissionEvaluator(permissionEvaluator);
        return customMethodSecurityExpressionHandler;
    }
    */
}
