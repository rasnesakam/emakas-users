package com.emakas.userService.service;

import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.TokenIntrospectionDto;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.mappers.TokenIntrospectionMapper;
import com.emakas.userService.model.Application;
import com.emakas.userService.model.Token;
import com.emakas.userService.model.User;
import com.emakas.userService.repository.UserTokenRepository;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.TokenTargetType;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TokenService{
    private final UserTokenRepository userTokenRepository;
    private final TokenManager tokenManager;
    private final UserService userService;
    private final TokenIntrospectionMapper tokenIntrospectionMapper;
    private final Logger logger = LoggerFactory.getLogger(TokenService.class);


    @Autowired
    public TokenService(UserTokenRepository repository, TokenManager tokenManager, UserService userService, TokenIntrospectionMapper tokenIntrospectionMapper) {
        this.userTokenRepository = repository;
        this.tokenManager = tokenManager;
        this.userService = userService;
        this.tokenIntrospectionMapper = tokenIntrospectionMapper;
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

    public Optional<Token> getFromSerializedToken(String token){
        return tokenManager.getFromToken(token);
    }
    public Optional<User> loadUserFromToken(Token token){
        return tokenManager.loadUserFromToken(token);
    }
}
