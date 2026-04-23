package com.emakas.userService.requestFilters;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.domain.auth.AuthorizationType;
import com.emakas.userService.domain.auth.UserPrincipal;
import com.emakas.userService.mappers.UserPrincipalMapper;
import com.emakas.userService.service.ClientCredentialsService;
import com.emakas.userService.service.TokenService;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.HashUtils;
import com.emakas.userService.shared.RequestUtils;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Güvenlik filtresinin en önde olmasını sağlar
public class AuthFilter extends OncePerRequestFilter {

    private final ClientCredentialsService clientCredentialsService;
    private final TokenService tokenService;
    private final UserPrincipalMapper userPrincipalMapper;

    public AuthFilter(@Lazy ClientCredentialsService clientCredentialsService, @Lazy TokenService tokenService, UserPrincipalMapper userPrincipalMapper) {
        this.clientCredentialsService = clientCredentialsService;
        this.tokenService = tokenService;
        this.userPrincipalMapper = userPrincipalMapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       resolveAuthentication(request).ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth));
       filterChain.doFilter(request, response);
    }

    private Optional<Authentication> resolveAuthentication(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            String[] authHeaderParameters = authorizationHeader.split(Constants.Chars.ONE_SPACE);
            if (authHeaderParameters.length == 2) {
                final AuthorizationType authorizationType = AuthorizationType.valueOf(authHeaderParameters[0].trim().toUpperCase()); //TODO: Should it be enum or string?
                String authorizationToken = authHeaderParameters[1];
                return switch (authorizationType) {
                    case BEARER -> getBearerTokenAuthentication(authorizationToken);
                    case BASIC -> getBasicAuthentication(authorizationToken);
                };
            }
            else logger.warn("Unsupported authorization header format");
        }
        else if (request.getCookies() != null){
            return getAuthenticationFromCookie(request);
        }
        return Optional.empty();
    }

    private Optional<Authentication> getBearerTokenAuthentication(String token) {

        TokenVerificationStatus tokenVerificationStatus = tokenService.verifyToken(token);
        if (tokenVerificationStatus != TokenVerificationStatus.SUCCESS)
            return Optional.empty();
        return tokenService.getFromSerializedToken(token)
                .map(userPrincipalMapper::fromUserToken)
                .map(JwtAuthentication::new);
    }

    private Optional<Authentication> getSignInTokenAuthentication(String token, HttpServletRequest request) {
        TokenVerificationStatus tokenVerificationStatus = tokenService.verifyToken(token);
        if (tokenVerificationStatus != TokenVerificationStatus.SUCCESS)
            return Optional.empty();
        String sessionFingerPrint = RequestUtils.getRequestFingerPrint(request);
        Optional<String> tokenFingerPrint = tokenService.getSessionFingerprintFromToken(token);
        if (tokenFingerPrint.isEmpty() || !tokenFingerPrint.get().equals(sessionFingerPrint))
            return Optional.empty();

        Optional<UUID> userId = tokenService.getSubjectFromToken(token).map(UUID::fromString);
        if (userId.isEmpty())
            return Optional.empty();
        Optional<String> username = tokenService.getUsernameFromToken(token);
        Optional<String> email = tokenService.getEmailFromToken(token);
        UserPrincipal userPrincipal = new UserPrincipal();

        userId.ifPresent(userPrincipal::setUserId);
        username.ifPresent(userPrincipal::setUsername);
        email.ifPresent(userPrincipal::setEmail);

        return Optional.of(new JwtAuthentication(userPrincipal));
    }

    private Optional<Authentication> getBasicAuthentication(String credentials64) {
        try {
            byte[] credentialsByte = Base64.getDecoder().decode(credentials64);
            final String[] credentials = new String(credentialsByte, StandardCharsets.UTF_8).split(Constants.Chars.SEPARATOR);
            if (credentials.length != 2)
                return Optional.empty();
            UUID clientId = UUID.fromString(credentials[0]);
            return clientCredentialsService.validateClient(clientId, credentials[1])
                    .map(principal -> new UsernamePasswordAuthenticationToken(principal, null, List.of()));
        } catch (IllegalArgumentException illegalArgumentException) {
            logger.error(illegalArgumentException.getLocalizedMessage());
            return Optional.empty();
        }
    }

    private Optional<Authentication> getAuthenticationFromCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(c -> Constants.AUTH_COOKIE_PARAMETER.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .flatMap(token -> this.getSignInTokenAuthentication(token, request));
    }
}
