package com.emakas.userService.service;

import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.TokenIntrospectionDto;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.mappers.TokenIntrospectionMapper;
import com.emakas.userService.model.Token;
import com.emakas.userService.model.User;
import com.emakas.userService.repository.UserTokenRepository;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.TokenTargetType;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {
    private final UserTokenRepository userTokenRepository;
    private final TokenManager tokenManager;
    private final UserService userService;
    private final TokenIntrospectionMapper tokenIntrospectionMapper;

    @Autowired
    public TokenService(UserTokenRepository repository, TokenManager tokenManager, UserService userService, TokenIntrospectionMapper tokenIntrospectionMapper) {
        this.userTokenRepository = repository;
        this.tokenManager = tokenManager;
        this.userService = userService;
        this.tokenIntrospectionMapper = tokenIntrospectionMapper;
    }
    public Token save(Token token) {
        return userTokenRepository.save(token);
    }
    public Collection<Token> saveBatch(Token... tokens) {
        return userTokenRepository.saveAll(Arrays.stream(tokens).toList());
    }
    @Cacheable(
            value = "introspection",
            key = "#token",
            unless = "#result == null || #result.active == false"
    )
    public Optional<TokenIntrospectionDto> introspect(String token) {
        Optional<Token> tokenOptional = tokenManager.getFromToken(token);
        return tokenOptional.map(parsedToken -> {
            TokenVerificationStatus verificationStatus = tokenManager.verifyJwtToken(parsedToken.getSerializedToken());
            if (verificationStatus == TokenVerificationStatus.SUCCESS) {
                userTokenRepository.findById(parsedToken.getJti()).map(persistedToken -> {
                    TokenIntrospectionDto introspectionDto = tokenIntrospectionMapper.toIntrospection(persistedToken);
                    introspectionDto.setActive(true);
                    return Optional.of(introspectionDto);
                });
            }
            return Optional.<TokenIntrospectionDto>empty();
        }).orElse(Optional.empty());
    }

}
