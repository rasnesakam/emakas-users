package com.emakas.userService.service;

import com.emakas.userService.domain.auth.UserPrincipal;
import com.emakas.userService.dto.TokenIntrospectionDto;
import com.emakas.userService.mappers.TokenIntrospectionMapper;
import com.emakas.userService.model.*;
import com.emakas.userService.repository.UserTokenRepository;
import com.emakas.userService.shared.RequestUtils;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TokenService {
    private final UserTokenRepository userTokenRepository;
    private final TokenManager tokenManager;
    private final TokenIntrospectionMapper tokenIntrospectionMapper;
    private final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private final String appDomainName;

    @Autowired
    public TokenService(UserTokenRepository repository, TokenManager tokenManager,
            TokenIntrospectionMapper tokenIntrospectionMapper, @Value("${app.domain}") String appDomainName) {
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
        TokenVerificationStatus verificationStatus = tokenManager.verifyJwtToken(token);
        if (verificationStatus == TokenVerificationStatus.SUCCESS) {
            AccessToken accessToken = tokenManager.parseAccessToken(token);
            TokenIntrospectionDto introspectionDtoValue = tokenIntrospectionMapper.toIntrospection(accessToken);
            introspectionDtoValue.setActive(true);
            return Optional.of(introspectionDtoValue);
        }
        return Optional.<TokenIntrospectionDto>empty();
    }

    public AccessToken createUserAccessToken(User user, String[] audiences, String[] scopes,
            Application requestedClient) {
        AccessToken token = tokenManager.createAccessToken(user, requestedClient, audiences, scopes);
        return userTokenRepository.save(token);
    }

    public RefreshToken createUserRefreshToken(UserLogin userLogin) {
        RefreshToken token = tokenManager.createRefreshToken(userLogin);
        return userTokenRepository.save(token);
    }

    public AccessToken createApplicationAccessToken(Application application, String[] audiences, String[] scopes) {
        return tokenManager.createAccessToken(application, audiences, scopes);
    }

    public RefreshToken createApplicationRefreshToken(Application application) {
        // TODO: Implement creating refresh token for app
        return new RefreshToken();
    }

    public IdToken createIdToken(User user) {
        // TODO: Implement creatint Id token
        // You need to create if SCOPE_OPENID provided. And provide other fields if
        // other scopes provided
        return new IdToken();
    }

    // TODO: You may find persisted solution for db. Or I don't know
    public String createSignInToken(UserPrincipal userPrincipal, HttpServletRequest request) {
        Map<String, String> customClaims = new TreeMap<>();
        customClaims.put("email", userPrincipal.getUsername());
        customClaims.put("user_name", userPrincipal.getUsername());
        customClaims.put("session_fingerprint", RequestUtils.getRequestFingerPrint(request));
        return tokenManager.createCustomToken(customClaims);
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

    public Optional<AccessToken> parseTokenAsAccessToken(String token) {
        try {
            return Optional.of(tokenManager.parseAccessToken(token));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public Optional<RefreshToken> parseTokenAsRefreshToken(String token) {
        try {
            return Optional.of(tokenManager.parseRefreshToken(token));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public String serializeToken(AccessToken token) {
        return tokenManager.serializeAccessToken(token);
    }

    public String serializeToken(RefreshToken token) {
        return tokenManager.serializeRefreshToken(token);
    }

    public String serializeToken(IdToken token) {
        return tokenManager.serializeIdToken(token);
    }

}
