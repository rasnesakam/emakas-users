package com.emakas.userService.service;

import com.auth0.jwt.interfaces.Claim;
import com.emakas.userService.domain.auth.UserPrincipal;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.TokenIntrospectionDto;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.mappers.TokenIntrospectionMapper;
import com.emakas.userService.model.Application;
import com.emakas.userService.model.Token;
import com.emakas.userService.model.User;
import com.emakas.userService.repository.UserTokenRepository;
import com.emakas.userService.shared.RequestUtils;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.TokenTargetType;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TokenService{
    private final UserTokenRepository userTokenRepository;
    private final TokenManager tokenManager;
    private final TokenIntrospectionMapper tokenIntrospectionMapper;
    private final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private final String appDomainName;


    @Autowired
    public TokenService(UserTokenRepository repository, TokenManager tokenManager, TokenIntrospectionMapper tokenIntrospectionMapper, @Value("${app.domain}") String appDomainName) {
        this.userTokenRepository = repository;
        this.tokenManager = tokenManager;
        this.tokenIntrospectionMapper = tokenIntrospectionMapper;
        this.appDomainName = appDomainName;
    }

    @CachePut(value = "tokens", key = "#result.jti")
    public Token save(Token token) {
        return userTokenRepository.save(token);
    }

    @Cacheable(value = "tokens", key = "#jti", unless = "#result.isEmpty()")
    public Optional<Token> getByJti(UUID jti) {
        return userTokenRepository.findById(jti);
    }

    @CachePut(value = "token_blacklist", key = "#token.jti")
    public boolean invalidateToken(Token token) {
        return true;
    }

    @Cacheable(value = "token_blacklist", key = "#token.jti")
    public boolean isTokenInBlacklist(Token token) {
        return false;
    }

    @Cacheable(value = "introspection", key = "#token", unless = "#result == null || #result.active == false")
    public Optional<TokenIntrospectionDto> introspect(String token) {
        Optional<Token> tokenOptional = tokenManager.getFromToken(token);
        return tokenOptional.map(parsedToken -> {
            TokenVerificationStatus verificationStatus = tokenManager.verifyJwtToken(parsedToken.getSerializedToken());
            if (verificationStatus == TokenVerificationStatus.SUCCESS) {
                 Optional<TokenIntrospectionDto> introspection = this.getByJti(parsedToken.getJti()).map(persistedToken -> {
                    TokenIntrospectionDto introspectionDtoValue = tokenIntrospectionMapper.toIntrospection(persistedToken);
                    introspectionDtoValue.setActive(true);
                    return introspectionDtoValue;
                 });
                 if (introspection.isEmpty())
                     logger.error(String.format(Locale.ENGLISH, "Token with jti '%s' coult not found", parsedToken.getJti()));
                 return introspection;
            }
            return Optional.<TokenIntrospectionDto>empty();
        }).orElse(Optional.empty());
    }
    public Token createUserAccessToken(User user, String[] audiences, String[] scopes, UUID requestedClientId) {
        Token token = tokenManager.createUserAccessToken(user, audiences, scopes, requestedClientId);
        return userTokenRepository.save(token);
    }
    public Token createUserRefreshToken(User user, String[] audiences, UUID requestedClientId) {
        Token token = tokenManager.createUserRefreshToken(user, requestedClientId, audiences);
        return userTokenRepository.save(token);
    }
    public Token createUserAccessToken(User user, String[] audiences, String[] scopes, Application requestedClient) {
        return createUserAccessToken(user, audiences, scopes, requestedClient.getId());
    }
    public Token createUserRefreshToken(User user, String[] audiences, Application requestedClient) {
        return createUserRefreshToken(user, audiences, requestedClient.getId());
    }
    public Token createApplicationAccessToken(Application application, String[] scopes) {
        return tokenManager.createApplicationAccessToken(application, new String[]{application.getUri()}, scopes, application.getId());
    }
    public Token createApplicationRefreshToken(Application application) {
        return tokenManager.createApplicationRefreshToken(application, application.getId(), new String[]{application.getUri()});
    }

    public String createSignInToken(UserPrincipal userPrincipal, HttpServletRequest request) {
        Map<String, String> customClaims = new TreeMap<>();
        customClaims.put("email", userPrincipal.getUsername());
        customClaims.put("user_name", userPrincipal.getUsername());
        customClaims.put("session_fingerprint", RequestUtils.getRequestFingerPrint(request));
        return tokenManager.generateCustomJwtToken(userPrincipal.getUserId().toString(), new String[]{appDomainName}, new String[]{"sign-in"}, Instant.now().plus(Duration.ofMinutes(10)).getEpochSecond(), customClaims);
    }

    public Optional<String> getSessionFingerprintFromToken(String token) {
        return tokenManager.getTokenClaim(token, "session_fingerprint", String.class);
    }

    public Optional<String> getSubjectFromToken(String token) {
        return tokenManager.getTokenClaim(token, "sub", String.class);
    }

    public Optional<String> getUsernameFromToken(String token) {
        return tokenManager.getTokenClaim(token, "sub", String.class);
    }

    public Optional<String> getEmailFromToken(String token) {
        return tokenManager.getTokenClaim(token, "sub", String.class);
    }

    public Optional<Set<String>> getScopesFromToken(String token) {
        return tokenManager.getTokenClaimAsList(token, "scope", String.class).map(HashSet::new);
    }

    public TokenVerificationStatus verifyToken(String token) {
        return tokenManager.verifyJwtToken(token);
    }

    public Optional<Token> getFromSerializedToken(String token){
        return tokenManager.getFromToken(token);
    }
    public Optional<User> loadUserFromToken(Token token){
        return tokenManager.loadUserFromToken(token);
    }
}
