package com.emakas.userService.config;

import com.emakas.userService.csrfTokenHandlers.SpaCsrfTokenRequestHandler;
import com.emakas.userService.handlers.UnauthorizedHandler;
import com.emakas.userService.requestFilters.AuthFilter;
import com.emakas.userService.service.ClientCredentialsService;
import com.emakas.userService.service.UserService;
import com.emakas.userService.shared.TokenManager;
import org.checkerframework.checker.nullness.qual.NonNull;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final UserService userService;
    private final AuthFilter authFilter;

    @Autowired
    public WebSecurityConfig(UserService userService, AuthFilter authFilter) {
        this.authFilter = authFilter;
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
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(config -> {
                    OrRequestMatcher orRequestMatcher = getAuthEndpointsRequestMatcher();
                    config.requireCsrfProtectionMatcher(orRequestMatcher);
                    CookieCsrfTokenRepository cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
                    cookieCsrfTokenRepository.setCookieCustomizer(customizer -> {
                        customizer.httpOnly(false);
                        customizer.maxAge(Duration.of(10, ChronoUnit.MINUTES));
                        customizer.sameSite("Strict");

                    });
                    config.csrfTokenRepository(cookieCsrfTokenRepository);
                    config.csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler());

                })
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            new AntPathRequestMatcher("/*"),
                            new AntPathRequestMatcher("/swagger-ui/**"),
                            new AntPathRequestMatcher("/v3/api-docs/**"),
                            new AntPathRequestMatcher("/assets/**"),
                            new AntPathRequestMatcher("/vectors/**"),
                            new AntPathRequestMatcher("/page/**"),
                            new AntPathRequestMatcher("/api/auth/**"),
                            new AntPathRequestMatcher("/api/apps/self"),
                            new AntPathRequestMatcher("/api/apps/info"),
                            new AntPathRequestMatcher("/oauth2/authorize"),
                            new AntPathRequestMatcher("/api/token/"),
                            new AntPathRequestMatcher("/api/token/introspect"),
                            new AntPathRequestMatcher("/.well-known/**")
                    ).permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //TODO: Replace our auth filter with default one
        /*
        http.addFilterAt(getAuthFilter(), AuthorizationFilter.class)
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll()
                );
         */
        return http.build();
    }

    private static @NonNull OrRequestMatcher getAuthEndpointsRequestMatcher() {
        AntPathRequestMatcher authPostMatcher = new AntPathRequestMatcher("/api/auth/**", HttpMethod.POST.name());
        AntPathRequestMatcher authPutMatcher = new AntPathRequestMatcher("/api/auth/**", HttpMethod.PUT.name());
        AntPathRequestMatcher authDeleteMatcher = new AntPathRequestMatcher("/api/auth/**", HttpMethod.DELETE.name());
        return new OrRequestMatcher(List.of(authPostMatcher, authPutMatcher, authDeleteMatcher));
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
