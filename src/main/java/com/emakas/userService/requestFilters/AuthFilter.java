package com.emakas.userService.requestFilters;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.model.UserToken;
import com.emakas.userService.service.ApplicationService;
import com.emakas.userService.service.UserService;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    private final UserService userService;

    private final ApplicationService applicationService;

    @Autowired
    public AuthFilter(TokenManager tokenManager, UserService userService, ApplicationService applicationService) {
        this.tokenManager = tokenManager;
        this.userService = userService;
        this.applicationService = applicationService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String url = request.getRequestURI();
        return !url.startsWith("/api") || url.startsWith("/api/auth") || url.startsWith("/api/oauth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            final String token = authorizationHeader.substring("Bearer ".length());
            switch (tokenManager.verifyJwtToken(token)){
                case SUCCESS:
                    Optional<UserToken> optionalUserToken = tokenManager.getFromToken(token);
                    if (optionalUserToken.isPresent()){
                        UserToken userToken = optionalUserToken.get();
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
