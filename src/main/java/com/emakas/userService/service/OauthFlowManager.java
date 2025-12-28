package com.emakas.userService.service;

import com.emakas.userService.domain.auth.ClientCredential;
import com.emakas.userService.domain.auth.ClientPrincipal;
import com.emakas.userService.domain.auth.ClientType;
import com.emakas.userService.dto.Response;
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
    private final TokenService tokenService;
    private final ResourcePermissionService resourcePermissionService;
    private final ApplicationService applicationService;
    private final ClientCredentialsService clientCredentialsService;

    @Autowired
    public OauthFlowManager(UserLoginService userLoginService, TokenService tokenService, ResourcePermissionService resourcePermissionService, ApplicationService applicationService, ClientCredentialsService clientCredentialsService) {
        this.userLoginService = userLoginService;
        this.tokenService = tokenService;
        this.resourcePermissionService = resourcePermissionService;
        this.applicationService = applicationService;
        this.clientCredentialsService = clientCredentialsService;
    }

    private ResponseEntity<TokenResponseDto> getTokenResponseFromUserLogin(UserLogin userLogin) {
        if (Instant.now().isAfter(Instant.ofEpochSecond(userLogin.getExpirationDateInSeconds())))
            return ResponseEntity.badRequest().build();
        User loggedUser = userLogin.getLoggedUser();
        Application requestedClient = userLogin.getRequestedClient();
        Token token = tokenService.createUserAccessToken(
                loggedUser,
                userLogin.getAuthorizedAudiences().toArray(String[]::new),
                userLogin.getAuthorizedScopes().toArray(String[]::new),
                requestedClient
        );
        Token refreshToken = tokenService.createUserRefreshToken(
                loggedUser,
                userLogin.getAuthorizedAudiences().toArray(String[]::new),
                requestedClient
        );
        long expiresAt = Duration.between(Instant.now(), Instant.ofEpochSecond(token.getExp())).toSeconds();
        TokenResponseDto dto =  new TokenResponseDto(
                loggedUser.getUserName(), loggedUser.getName(), loggedUser.getSurname(),
                loggedUser.getEmail(), token.getSerializedToken(), expiresAt, refreshToken.getSerializedToken(),
                Constants.BEARER_TOKEN
        );
        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<TokenResponseDto> handleAuthorizationFlow(String grant, UUID clientId, String clientSecret, String codeVerifier, String redirectUri) {
        if (Objects.isNull(clientSecret) && Objects.nonNull(codeVerifier))
            return handlePCKEFlow(grant, codeVerifier, clientId);
        Optional<Application> applicationValue = applicationService.getById(clientId);
        Optional<ClientPrincipal> clientCredential = clientCredentialsService.validateClient(clientId, clientSecret);
        if (applicationValue.isPresent() && clientCredential.isPresent() && applicationValue.get().getRedirectUri().equals(redirectUri)) {
            Optional<UserLogin> userLogin = userLoginService.getUserLoginByGrant(grant);
            return userLogin.map(this::getTokenResponseFromUserLogin).orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    public ResponseEntity<TokenResponseDto> handleRefreshTokenFlow(String refreshToken, HttpServletRequest request) {
        Optional<Token> tokenInput = tokenService.getFromSerializedToken(refreshToken);
        if (tokenInput.isPresent())
        {
            Token token = tokenInput.get();
            String origin = request.getHeader(HttpHeaders.ORIGIN);
            if (token.getAud().stream().anyMatch(audience -> audience.equals(origin))){
                Optional<User> optionalUser = tokenService.loadUserFromToken(token);
                if (optionalUser.isEmpty())
                    return ResponseEntity.badRequest().build();
                User user = optionalUser.get();
                String[] audiences = token.getAud().toArray(String[]::new);
                String[] scopes = resourcePermissionService.getPermissionsByUser(user)
                        .stream().map(ResourcePermission::toString).toArray(String[]::new);
                Token newAccessToken = tokenService.createUserAccessToken(user, audiences, scopes, token.getClientId());
                Token newRefreshToken = tokenService.createUserRefreshToken(user, audiences, token.getClientId());
                long expiresAt = Duration.between(Instant.now(), Instant.ofEpochSecond(newAccessToken.getExp())).toSeconds();
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
        Optional<ClientPrincipal> clientPrincipal = clientCredentialsService.validateClient(clientId, clientSecret);
        return clientPrincipal.map(cp -> {
            if (cp.getClientType() != ClientType.APPLICATION) //TODO: Think about non applications (i.e, Resources)
                return ResponseEntity.badRequest().<TokenResponseDto>build();
            Optional<Application> application = applicationService.getById(cp.getClientId());
            return application.map(app -> {
                Token applicationAccessToken = tokenService.createApplicationAccessToken(app, requestedScopes);
                Token applicationRefreshToken = tokenService.createApplicationRefreshToken(app);
                TokenResponseDto tokenResponseDto = new TokenResponseDto(
                        app.getName(), null, null, null,
                        applicationAccessToken.getSerializedToken(),
                        Duration.between(Instant.now(), Instant.ofEpochSecond(applicationAccessToken.getExp())).toSeconds(),
                        applicationRefreshToken.getSerializedToken(),Constants.BEARER_TOKEN
                );
                return ResponseEntity.ok(tokenResponseDto);
            }).orElseGet(() -> ResponseEntity.notFound().build());
        }).orElseGet(() -> ResponseEntity.notFound().build());
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
