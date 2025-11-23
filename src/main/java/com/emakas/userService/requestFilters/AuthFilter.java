package com.emakas.userService.requestFilters;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.model.Token;
import com.emakas.userService.service.ApplicationService;
import com.emakas.userService.service.UserService;
import com.emakas.userService.shared.TokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AuthFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    public AuthFilter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
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
                new AntPathRequestMatcher("/api/apps/self")
        );

        OrRequestMatcher blackListedRequestMatcher = new OrRequestMatcher(blackListedPaths);
        if (blackListedRequestMatcher.matches(request)){
            logger.info(String.format("matches %s", request.getRequestURI()));
            return true;
        }
        logger.info(String.format("not matches %s", request.getRequestURI()));
        return false;
    }

/*
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String url = request.getRequestURI();

        return !url.startsWith("/api") || url.startsWith("/api/auth") || (url.startsWith("/api/oauth") && !url.startsWith("/api/oauth/token/verify"));
    }

 */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("Filtering request");
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            final String token = authorizationHeader.substring("Bearer ".length());
            switch (tokenManager.verifyJwtToken(token)){
                case SUCCESS:
                    Optional<Token> optionalUserToken = tokenManager.getFromToken(token);
                    if (optionalUserToken.isPresent()){
                        Token userToken = optionalUserToken.get();
                        JwtAuthentication jwtAuthentication = new JwtAuthentication(userToken);
                        SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
                        filterChain.doFilter(request,response);
                    }
                    else
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    break;
                case FAILED:
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is not verified.");
                    break;
                case EXPIRED:
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired, please get new token.");
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token could not verified. Try other token or try again later.");
            };
        }
        else
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is not verified.");
    }
}
