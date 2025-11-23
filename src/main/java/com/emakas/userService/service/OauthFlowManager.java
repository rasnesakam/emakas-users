package com.emakas.userService.service;

import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.model.*;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.PkceOperationsManager;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.CodeChallengeMethod;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class OauthFlowManager {
    private final UserLoginService userLoginService;
    private final TokenManager tokenManager;
    private final TokenService tokenService;
    private final ResourcePermissionService resourcePermissionService;
    private final ApplicationService applicationService;

    @Autowired
    public OauthFlowManager(UserLoginService userLoginService, TokenManager tokenManager, TokenService tokenService, ResourcePermissionService resourcePermissionService, ApplicationService applicationService) {
        this.userLoginService = userLoginService;
        this.tokenManager = tokenManager;
        this.tokenService = tokenService;
        this.resourcePermissionService = resourcePermissionService;
        this.applicationService = applicationService;
    }

    private ResponseEntity<TokenResponseDto> getTokenResponseFromUserLogin(UserLogin userLogin) {
        if (Instant.now().isAfter(Instant.ofEpochSecond(userLogin.getExpirationDateInSeconds())))
            return ResponseEntity.badRequest().build();
        User loggedUser = userLogin.getLoggedUser();
        Token token = tokenManager.createUserAccessToken(
                loggedUser,
                userLogin.getAuthorizedAudiences().toArray(String[]::new),
                userLogin.getAuthorizedScopes().toArray(String[]::new),
                userLogin.getRequestedClient().getId()
        );
        Token refreshToken = tokenManager.createUserRefreshToken(
                loggedUser,
                userLogin.getRequestedClient().getId(),
                userLogin.getAuthorizedAudiences().toArray(String[]::new)
        );
        tokenService.saveBatch(token, refreshToken);
        long expiresAt = Duration.between(Instant.now(), Instant.ofEpochSecond(token.getExp())).toSeconds();
        TokenResponseDto dto =  new TokenResponseDto(
                loggedUser.getUserName(), loggedUser.getName(), loggedUser.getSurname(),
                loggedUser.getEmail(), token.getSerializedToken(), expiresAt, refreshToken.getSerializedToken(),
                Constants.BEARER_TOKEN
        );
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<TokenResponseDto> handleAuthorizationFlow(String grant, UUID clientId, String clientSecret, String codeVerifier, String redirectUri) {
        //TODO: Implement client id and secret mechanism
        if (Objects.isNull(clientSecret) && Objects.nonNull(codeVerifier))
            return handlePCKEFlow(grant, codeVerifier, clientId);
        Optional<Application> applicationValue = applicationService.getById(clientId);
        if (applicationValue.isPresent() && applicationValue.get().getRedirectUri().equals(redirectUri)) {
            Optional<UserLogin> userLogin = userLoginService.getUserLoginByGrant(grant);
            return userLogin.map(this::getTokenResponseFromUserLogin).orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    public ResponseEntity<TokenResponseDto> handleRefreshTokenFlow(String refreshToken, HttpServletRequest request) {
        Optional<Token> tokenInput = tokenManager.getFromToken(refreshToken);
        if (tokenInput.isPresent())
        {
            Token token = tokenInput.get();
            String origin = request.getHeader(HttpHeaders.ORIGIN);
            if (token.getAud().stream().anyMatch(audience -> audience.equals(origin))){
                Optional<User> optionalUser = tokenManager.loadUserFromToken(token);
                if (optionalUser.isEmpty())
                    return ResponseEntity.badRequest().build();
                User user = optionalUser.get();
                String[] audiences = token.getAud().toArray(String[]::new);
                String[] scopes = resourcePermissionService.getPermissionsByUser(user)
                        .stream().map(ResourcePermission::toString).toArray(String[]::new);
                Token newAccessToken = tokenManager.createUserAccessToken(user, audiences, scopes, token.getClientId());
                Token newRefreshToken = tokenManager.createUserRefreshToken(user, token.getClientId(),audiences);
                long expiresAt = Duration.between(Instant.now(), Instant.ofEpochSecond(newAccessToken.getExp())).toSeconds();
                tokenService.saveBatch(newAccessToken, newRefreshToken);
                TokenResponseDto tokenResponseDto = new TokenResponseDto(
                        user.getUserName(), user.getName(), user.getSurname(),
                        user.getEmail(), newAccessToken.getSerializedToken(), expiresAt, newRefreshToken.getSerializedToken(),
                        Constants.BEARER_TOKEN
                );
                return ResponseEntity.ok(tokenResponseDto);
            }

        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<TokenResponseDto> handleClientCredentialsFlow(UUID clientId, String clientSecret, String[] requestedScopes){
        //TODO: Implement Client Credentials Flow
        return null;
    }

    public ResponseEntity<TokenResponseDto> handlePCKEFlow(String grant , String codeVerifier, UUID clientId){
        Optional<UserLogin> userLoginValue = userLoginService.getUserLoginByGrant(grant);
        if (userLoginValue.isPresent()){
            UserLogin userlogin = userLoginValue.get();
            Optional<Application> applicationValue = applicationService.getById(clientId);
            if (applicationValue.isPresent()){
                String codeChallenge = userlogin.getCodeChallenge();
                CodeChallengeMethod challengeMethod = userlogin.getCodeChallengeMethod();
                if (PkceOperationsManager.checkCodeChallenge(codeChallenge, codeVerifier, challengeMethod)){
                    return getTokenResponseFromUserLogin(userlogin);
                }
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.notFound().build();
    }
}
