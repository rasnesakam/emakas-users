package com.emakas.userService.service;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import com.emakas.userService.model.*;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.SecurityContextManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import com.emakas.userService.config.OidcConfig;
import com.emakas.userService.dto.AuthorizeDto;
import com.emakas.userService.dto.LoginPageInput;
import com.emakas.userService.mappers.LoginPageInputMapper;
import com.emakas.userService.shared.exceptions.AuthorizeFlowException;

/**
 * Handles base authentication flows such as redirecting login page and client
 * callbacks
 * 
 * @author Ensar Makas
 */
@Component
public class AuthorizeFlowService {

    private final ApplicationService applicationService;
    private final OidcConfig oidcConfig;
    private final UserLoginService userLoginService;
    private final LoginSessionService loginSessionService;
    private final LoginPageInputMapper loginPageInputMapper;
    private final SecurityContextManager securityContextManager;
    private final OidcDiscoveryService oidcDiscoveryService;

    @Autowired
    public AuthorizeFlowService(ApplicationService applicationService, OidcConfig oidcConfig,
            UserLoginService userLoginService, LoginSessionService loginSessionService,
            LoginPageInputMapper loginPageInputMapper, SecurityContextManager securityContextManager,
            OidcDiscoveryService oidcDiscoveryService) {
        this.applicationService = applicationService;
        this.oidcConfig = oidcConfig;
        this.userLoginService = userLoginService;
        this.loginSessionService = loginSessionService;
        this.loginPageInputMapper = loginPageInputMapper;
        this.securityContextManager = securityContextManager;
        this.oidcDiscoveryService = oidcDiscoveryService;
    }

    /**
     * Entrypoint of authorization flow
     * Redirects to login page if no authentication provided
     * redirects to client callback page if authentication and authorization
     * provided
     * Also validates if login request is valid for provided client and scope
     * informations
     * 
     * @param authorizeDto inputs of login page according to OAuth 2.0 specs
     * @return Response Entity to redirect request to different endpoints and pages
     */
    public ResponseEntity<Object> handleAuthorizationFlow(AuthorizeDto authorizeDto) {
        try {
            if (authorizeDto.getSessionId() != null)
                return this.redirectClientCallbackPage(authorizeDto.getSessionId());

            if (authorizeDto.getClientId() == null)
                throw new AuthorizeFlowException("Bad Client Id", "Please provide a valid client id");

            Application clientInfo = applicationService.getById(authorizeDto.getClientId())
                    .orElseThrow(() -> new AuthorizeFlowException("Bad Client", "Please provide a valid client"));

            Set<String> supportedScopes = Set.of(oidcDiscoveryService.getConfigurationForTenant(
                    clientInfo.getTenant().getId().toString()).getScopesSupported());

            if (authorizeDto.getScope() == null || authorizeDto.getScope().isBlank())
                throw new AuthorizeFlowException("Bad Scope", "You should provide which scopes you want to");

            if (Arrays.stream(authorizeDto.getScope().split(Constants.Chars.ONE_SPACE))
                    .anyMatch(s -> !supportedScopes.contains(s)))
                throw new AuthorizeFlowException("Bad Scope", "System does not support these scopes");

            return this.redirectLoginPageIfNoAuth(loginPageInputMapper.fromAuthorizeDto(authorizeDto));

        } catch (AuthorizeFlowException e) {
            URI redirectTo = UriComponentsBuilder.fromUriString("/page/error")
                    .queryParam("title", e.getTitle())
                    .queryParam("description", e.getDescription())
                    .build().toUri();
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectTo.toString()).build();
        }
    }

    /**
     * Redirects if no authentication provided
     * 
     * @param loginPageInput
     * @return
     */
    public ResponseEntity<Object> redirectLoginPageIfNoAuth(LoginPageInput loginPageInput) {

        Optional<Application> application = applicationService.getById(loginPageInput.getClientId());
        if (application.isEmpty())
            throw new RuntimeException("Client not found, should not happen due to previous checks");
        // Mapper denenebilir belki
        LoginSession loginSession = new LoginSession(
                application.get(), Set.of(loginPageInput.getScope().split(Constants.Chars.ONE_SPACE)),
                Set.of(loginPageInput.getClientId().toString()),
                loginPageInput.getRedirectUri(), loginPageInput.getState(), loginPageInput.getResponseType(),
                loginPageInput.getCodeChallenge(),
                loginPageInput.getCodeChallengeMethodString(), Duration.ofMinutes(10).toMillis());

        LoginSession savedLoginSession = loginSessionService.save(loginSession);

        String scheme = loginPageInput.getRequest().getScheme();
        String host = loginPageInput.getRequest().getServerName();
        int port = loginPageInput.getRequest().getServerPort();

        UriComponentsBuilder baseUri = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host);

        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            baseUri.port(port);
        }
        String fromUriString = baseUri.cloneBuilder()
                .path("/oauth2/authorize")
                .queryParam("session_id", savedLoginSession.getId())
                .build()
                .toUriString();
        String decodedUrlString = URLDecoder.decode(fromUriString, StandardCharsets.UTF_8);
        URI redirectTo = UriComponentsBuilder.fromPath("/page/auth/login")
                .queryParam("continue", URLEncoder.encode(decodedUrlString, StandardCharsets.UTF_8))
                .build()
                .encode()
                .toUri();
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectTo.toString()).build();
    }

    /**
     * Creates a grant code and sends it into client's callback page
     * 
     * @param loginSessionId
     * @return
     * @throws AuthorizeFlowException
     */
    public ResponseEntity<Object> redirectClientCallbackPage(UUID loginSessionId) throws AuthorizeFlowException {
        Optional<LoginSession> loginSession = loginSessionService.getById(loginSessionId);
        if (loginSession.isEmpty())
            throw new AuthorizeFlowException("Invalid Request", "Login session not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken)
            throw new AuthorizeFlowException("Invalid Request",
                    "Authentication required to proceed in authorization flow");

        Optional<User> loggedUser = securityContextManager.getCurrentUser();
        if (loggedUser.isEmpty())
            throw new AuthorizeFlowException("Invalid Request", "User not found");

        UserLogin userLogin = new UserLogin(loginSession.get(), loggedUser.get());
        UserLogin savedUserLogin = userLoginService.save(userLogin);

        URI redirectTo = UriComponentsBuilder.fromUriString(loginSession.get().getRequestedClient().getRedirectUri())
                .queryParam("code", savedUserLogin.getAuthorizationGrant())
                .queryParam("state", loginSession.get().getState())
                .build().toUri();
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectTo.toString()).build();
    }
}
