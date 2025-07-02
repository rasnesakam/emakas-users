package com.emakas.userService.service;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.model.ResourcePermission;
import com.emakas.userService.model.Token;
import com.emakas.userService.model.User;
import com.emakas.userService.model.UserLogin;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.TokenManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class OauthFlowManager {
    private final UserLoginService userLoginService;
    private final TokenManager tokenManager;
    private final TokenService tokenService;
    private final ResourcePermissionService resourcePermissionService;

    @Autowired
    public OauthFlowManager(UserLoginService userLoginService, TokenManager tokenManager, TokenService tokenService, ResourcePermissionService resourcePermissionService) {
        this.userLoginService = userLoginService;
        this.tokenManager = tokenManager;
        this.tokenService = tokenService;
        this.resourcePermissionService = resourcePermissionService;
    }

    public ResponseEntity<Response<TokenResponseDto>> handleAccessTokenFlow(String grant, String clientId, String clientSecret) {
        //TODO: Implement client id and secret mechanism
        Optional<UserLogin> userLogin = userLoginService.getUserLoginByGrant(grant);
        if (userLogin.isPresent()){
            User loggedUser = userLogin.get().getLoggedUser();
            Token token = tokenManager.createUserAccessToken(
                    loggedUser,
                    userLogin.get().getAuthorizedAudiences().toArray(String[]::new),
                    userLogin.get().getAuthorizedScopes().toArray(String[]::new)
            );
            Token refreshToken = tokenManager.createUserRefreshToken(loggedUser, userLogin.get().getAuthorizedAudiences().toArray(String[]::new));
            tokenService.saveBatch(token, refreshToken);
            long expiresAt = Duration.between(Instant.now(), Instant.ofEpochSecond(token.getExp())).toSeconds();
            TokenResponseDto tokenResponseDto = new TokenResponseDto(
                    loggedUser.getUserName(), loggedUser.getName(), loggedUser.getSurname(),
                    loggedUser.getEmail(), token.getSerializedToken(), expiresAt, refreshToken.getSerializedToken(),
                    Constants.BEARER_TOKEN
            );
            return new ResponseEntity<>(new Response<>(tokenResponseDto), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response<>(null, "Invalid grant"),HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Response<TokenResponseDto>> handleRefreshTokenFlow(String refreshToken, HttpServletRequest request) {
        Optional<Token> tokenInput = tokenManager.getFromToken(refreshToken);
        if (tokenInput.isPresent())
        {
            Token token = tokenInput.get();
            String origin = request.getHeader(HttpHeaders.ORIGIN);
            if (token.getAud().stream().anyMatch(audience -> audience.equals(origin))){
                Optional<User> optionalUser = tokenManager.loadUserFromToken(token);
                if (optionalUser.isEmpty())
                    return new ResponseEntity<>(Response.of("Invalid Token"), HttpStatus.BAD_REQUEST);
                User user = optionalUser.get();
                String[] audiences = token.getAud().toArray(String[]::new);
                String[] scopes = resourcePermissionService.getPermissionsByUser(user)
                        .stream().map(ResourcePermission::toString).toArray(String[]::new);
                Token newAccessToken = tokenManager.createUserAccessToken(user, audiences, scopes);
                Token newRefreshToken = tokenManager.createUserRefreshToken(user, audiences);
                long expiresAt = Duration.between(Instant.now(), Instant.ofEpochSecond(newAccessToken.getExp())).toSeconds();
                tokenService.saveBatch(newAccessToken, newRefreshToken);
                TokenResponseDto tokenResponseDto = new TokenResponseDto(
                        user.getUserName(), user.getName(), user.getSurname(),
                        user.getEmail(), newAccessToken.getSerializedToken(), expiresAt, newRefreshToken.getSerializedToken(),
                        Constants.BEARER_TOKEN
                );
                return new ResponseEntity<>(Response.of(tokenResponseDto), HttpStatus.OK);
            }

        }
        return new ResponseEntity<>(Response.of("Invalid Token"),HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Response<TokenResponseDto>> handleClientCredentialsFlow(String clientId, String clientSecret){
        //TODO: Will be implemented
        return null;
    }
}
