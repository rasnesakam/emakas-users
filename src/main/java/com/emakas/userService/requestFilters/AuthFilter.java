package com.emakas.userService.requestFilters;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.domain.auth.AuthorizationType;
import com.emakas.userService.service.ClientCredentialsService;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;
    private final ClientCredentialsService clientCredentialsService;

    public AuthFilter(TokenManager tokenManager, ClientCredentialsService clientCredentialsService) {
        this.tokenManager = tokenManager;
        this.clientCredentialsService = clientCredentialsService;
    }

    //TODO: code again according to AntMatcher

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) throws ServletException {
        List<RequestMatcher> blackListedPaths = List.of(
                new AntPathRequestMatcher("/*"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/assets/**"),
                new AntPathRequestMatcher("/vectors/**"),
                new AntPathRequestMatcher("/page/**"),
                new AntPathRequestMatcher("/api/auth/**"),
                new AntPathRequestMatcher("/api/oauth/token"),
                new AntPathRequestMatcher("/api/apps/self"),
                new AntPathRequestMatcher("/api/apps/info")
        );

        OrRequestMatcher blackListedRequestMatcher = new OrRequestMatcher(blackListedPaths);
        if (blackListedRequestMatcher.matches(request)){
            logger.info(String.format("matches %s", request.getRequestURI()));
            return true;
        }
        logger.info(String.format("not matches %s", request.getRequestURI()));
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("Filtering request");
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
            return;
        }
        final String[] authorization = authorizationHeader.split(Constants.ONE_SPACE);
        if (authorization.length != 2 ){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication scheme.");
            return;
        }
        try {
            final AuthorizationType authorizationType = AuthorizationType.valueOf(authorization[0].trim().toUpperCase());

            Optional<Authentication> authentication = switch (authorizationType) {
                case BEARER -> {
                    final String tokenString = authorization[1].trim();
                    TokenVerificationStatus tokenVerificationStatus = tokenManager.verifyJwtToken(tokenString);
                    if (tokenVerificationStatus != TokenVerificationStatus.SUCCESS)
                        yield Optional.empty();
                    yield tokenManager.getFromToken(tokenString).map(JwtAuthentication::new);
                }
                case BASIC -> {
                    try {
                        final String credentials64 = authorization[1].trim();
                        byte[] credentialsByte = Base64.getDecoder().decode(credentials64);
                        final String[] credentials = new String(credentialsByte, StandardCharsets.UTF_8).split(Constants.SEPARATOR);
                        if (credentials.length != 2)
                            yield Optional.empty();
                        UUID clientId = UUID.fromString(credentials[0]);
                        yield clientCredentialsService.validateClient(clientId, credentials[1]).map(principal -> {
                            return new UsernamePasswordAuthenticationToken(principal, null, List.of());
                        });
                    } catch (IllegalArgumentException illegalArgumentException) {
                        logger.error(illegalArgumentException.getLocalizedMessage());
                        yield Optional.empty();
                    }
                }
            };

            if (authentication.isPresent()){
                Authentication authenticationValue = authentication.get();
                SecurityContextHolder.getContext().setAuthentication(authenticationValue);
                filterChain.doFilter(request, response);
            }
            else
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Couldn't authenticate");
        }catch (IllegalArgumentException e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authorization type.");
        }
    }
}
