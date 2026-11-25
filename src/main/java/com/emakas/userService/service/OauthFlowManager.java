package com.emakas.userService.service;

import com.emakas.userService.domain.auth.ClientPrincipal;
import com.emakas.userService.domain.auth.ClientType;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.model.*;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.PkceOperationsManager;
import com.emakas.userService.shared.enums.CodeChallengeMethod;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.NonNull;
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

/**
 * Handles OAuth logic flows such as generating access tokens, refresh tokens
 * and id tokens
 * 
 * @author Ensar Makas
 */
@Service
public class OauthFlowManager {
    private final UserLoginService userLoginService;
    private final TokenService tokenService;
    private final ApplicationService applicationService;
    private final ClientCredentialsService clientCredentialsService;
    private final UserPermissionService userPermissionService;

    @Autowired
    public OauthFlowManager(UserLoginService userLoginService, TokenService tokenService,
            ApplicationService applicationService, ClientCredentialsService clientCredentialsService,
            UserPermissionService userPermissionService) {
        this.userLoginService = userLoginService;
        this.tokenService = tokenService;
        this.applicationService = applicationService;
        this.clientCredentialsService = clientCredentialsService;
        this.userPermissionService = userPermissionService;
    }

    /**
     * Creates tokens and returns
     * 
     * @param userLogin authenticated login session
     * @return token response that contains id tokens, access tokens and refresh
     *         tokens
     */
    // TODO: bind refresh token with userlogin so when we recreate token with that
    // refresh token, use that userlogin again.
    private ResponseEntity<TokenResponseDto> getTokenResponseFromUserLogin(UserLogin userLogin) {
        if (Instant.now().isAfter(Instant.ofEpochSecond(userLogin.getExpirationDateInSeconds())))
            return ResponseEntity.badRequest().build();
        User loggedUser = userLogin.getLoggedUser();
        Application requestedClient = userLogin.getRequestedClient();
        AccessToken accessToken = tokenService.createUserAccessToken(
                loggedUser,
                userLogin.getAuthorizedAudiences().toArray(String[]::new),
                userPermissionService.modifyScopesByUserPrivillage(loggedUser, userLogin.getAuthorizedScopes()),
                requestedClient);
        RefreshToken refreshToken = tokenService.createUserRefreshToken(userLogin);
        IdToken idToken = tokenService.createIdToken(loggedUser);
        return getTokenResponseDtoResponseEntity(loggedUser, accessToken, refreshToken, idToken);
    }

    @NonNull
    private ResponseEntity<TokenResponseDto> getTokenResponseDtoResponseEntity(User loggedUser, AccessToken accessToken,
            RefreshToken refreshToken, IdToken idToken) {
        long expiresAt = Duration.between(Instant.now(), Instant.ofEpochSecond(accessToken.getExp())).toSeconds();
        TokenResponseDto dto = new TokenResponseDto();
        dto.setUsername(loggedUser.getUserName());
        dto.setName(loggedUser.getName());
        dto.setSurname(loggedUser.getSurname());
        dto.setEmail(loggedUser.getEmail());
        dto.setAccessToken(tokenService.serializeToken(accessToken));
        dto.setExpiresIn(expiresAt);
        dto.setRefreshToken(tokenService.serializeToken(refreshToken));
        dto.setTokenType(Constants.BEARER_TOKEN);
        if (idToken != null)
            dto.setIdToken(tokenService.serializeToken(idToken));
        return ResponseEntity.ok(dto);
    }

    /**
     * Entrypoint of the authorization flow. redirect request to specific flows such
     * as PKCE auth flow or basic client secret flow
     * 
     * @param grant        code that generated after success login form submission
     * @param clientId     identifier of client
     * @param clientSecret secret that indicates whether the client is sending this
     *                     request
     * @param codeVerifier provides if PKCE flow intended to use
     * @param redirectUri  a uri that redirects after login form submission
     * @return token response that contains token informations
     */
    public ResponseEntity<TokenResponseDto> handleAuthorizationFlow(String grant, UUID clientId, String clientSecret,
            String codeVerifier, String redirectUri) {
        if (Objects.isNull(clientSecret) && Objects.nonNull(codeVerifier))
            return handlePCKEFlow(grant, codeVerifier, clientId);
        Optional<Application> applicationValue = applicationService.getById(clientId);
        Optional<ClientPrincipal> clientCredential = clientCredentialsService.validateClient(clientId, clientSecret);
        if (applicationValue.isPresent() && clientCredential.isPresent()
                && applicationValue.get().getRedirectUri().equals(redirectUri)) {
            Optional<UserLogin> userLogin = userLoginService.getUserLoginByGrant(grant);
            return userLogin.map(this::getTokenResponseFromUserLogin)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    public ResponseEntity<TokenResponseDto> handleRefreshTokenFlow(String refreshToken, HttpServletRequest request) {
        Optional<RefreshToken> tokenInput = tokenService.parseTokenAsRefreshToken(refreshToken);
        if (tokenInput.isPresent()) {
            RefreshToken token = tokenInput.get();
            String origin = request.getHeader(HttpHeaders.ORIGIN);
            // TODO: we should validate if the token come from the origin where we intended
            // to. To do this, we need to add aud claim for token. Do we need to add aud
            // claim for Id tokens?
            // According to standard, there is no standard for refresh tokens. It could be
            // even random string.
            // But we can move on with UserLogin->Application relation.
            Optional<UserLogin> userLoginValue = userLoginService.getById(token.getUserLogin().getId());
            if (userLoginValue.isPresent()) {
                UserLogin userLogin = userLoginValue.get();
                User user = userLogin.getLoggedUser();
                String[] audiences = userLogin.getAuthorizedAudiences().toArray(new String[0]);
                String[] scopes = userLogin.getAuthorizedScopes().toArray(new String[0]);
                AccessToken newAccessToken = tokenService.createUserAccessToken(user, audiences, scopes,
                        userLogin.getRequestedClient());
                RefreshToken newRefreshToken = tokenService.createUserRefreshToken(userLogin);
                IdToken idToken = tokenService.createIdToken(user);
                return getTokenResponseDtoResponseEntity(user, newAccessToken, newRefreshToken, idToken);
            }

        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<TokenResponseDto> handleClientCredentialsFlow(UUID clientId, String clientSecret,
            String[] requestedScopes, String[] audiences) {
        Optional<ClientPrincipal> clientPrincipal = clientCredentialsService.validateClient(clientId, clientSecret);
        return clientPrincipal.map(cp -> {
            if (cp.getClientType() != ClientType.APPLICATION) // TODO: Think about non applications (i.e, Resources)
                return ResponseEntity.badRequest().<TokenResponseDto>build();
            Optional<Application> application = applicationService.getById(cp.getClientId());
            return application.map(app -> {
                AccessToken applicationAccessToken = tokenService.createApplicationAccessToken(app, audiences,
                        requestedScopes);
                RefreshToken applicationRefreshToken = tokenService.createApplicationRefreshToken(app);
                TokenResponseDto tokenResponseDto = new TokenResponseDto();
                tokenResponseDto.setName(app.getName());
                tokenResponseDto.setAccessToken(tokenService.serializeToken(applicationAccessToken));
                tokenResponseDto.setExpiresIn(Duration
                        .between(Instant.now(), Instant.ofEpochSecond(applicationAccessToken.getExp())).toSeconds());
                tokenResponseDto.setRefreshToken(tokenService.serializeToken(applicationRefreshToken));
                return ResponseEntity.ok(tokenResponseDto);
            }).orElseGet(() -> ResponseEntity.notFound().build());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<TokenResponseDto> handlePCKEFlow(String grant, String codeVerifier, UUID clientId) {
        Optional<UserLogin> userLoginValue = userLoginService.getUserLoginByGrant(grant);
        if (userLoginValue.isPresent()) {
            UserLogin userlogin = userLoginValue.get();
            Optional<Application> applicationValue = applicationService.getById(clientId);
            if (applicationValue.isPresent()) {
                String codeChallenge = userlogin.getCodeChallenge();
                CodeChallengeMethod challengeMethod = userlogin.getCodeChallengeMethod();
                if (PkceOperationsManager.checkCodeChallenge(codeChallenge, codeVerifier, challengeMethod)) {
                    return getTokenResponseFromUserLogin(userlogin);
                }
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.notFound().build();
    }
}
