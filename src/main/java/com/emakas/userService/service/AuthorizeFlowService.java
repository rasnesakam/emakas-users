package com.emakas.userService.service;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.emakas.userService.domain.auth.UserPrincipal;
import com.emakas.userService.shared.SecurityContextManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.emakas.userService.config.OidcConfig;
import com.emakas.userService.dto.AuthorizeDto;
import com.emakas.userService.dto.LoginPageInput;
import com.emakas.userService.mappers.LoginPageInputMapper;
import com.emakas.userService.model.Application;
import com.emakas.userService.model.LoginSession;
import com.emakas.userService.model.User;
import com.emakas.userService.model.UserLogin;
import com.emakas.userService.shared.exceptions.AuthorizeFlowException;

@Component
public class AuthorizeFlowService {

    private final ApplicationService applicationService;
    private final OidcConfig oidcConfig;
    private final UserLoginService userLoginService;
    private final UserService userService;
    private final LoginSessionService loginSessionService;
    private final LoginPageInputMapper loginPageInputMapper;
    private final SecurityContextManager securityContextManager;

    @Autowired
    public AuthorizeFlowService(ApplicationService applicationService, OidcConfig oidcConfig,
                                UserLoginService userLoginService, UserService userService, LoginSessionService loginSessionService,
                                LoginPageInputMapper loginPageInputMapper, SecurityContextManager securityContextManager) {
        this.applicationService = applicationService;
        this.oidcConfig = oidcConfig;
        this.userLoginService = userLoginService;
        this.userService = userService;
        this.loginSessionService = loginSessionService;
        this.loginPageInputMapper = loginPageInputMapper;
        this.securityContextManager = securityContextManager;
    }

    public ResponseEntity<Object> handleAuthorizationFlow(AuthorizeDto authorizeDto) {
        try {
            if (authorizeDto.getSessionId() != null)
                return this.redirectClientCallbackPage(authorizeDto.getSessionId());

            if (authorizeDto.getClientId() == null)
                throw new AuthorizeFlowException("Bad Client Id", "Please provide a valid client id");

            if (!oidcConfig.isScopeSupported(authorizeDto.getScope()))
                throw new AuthorizeFlowException("Bad Scope", "System does not support these scopes");

            Optional<Application> clientInfo = applicationService.getById(authorizeDto.getClientId());
            if (clientInfo.isEmpty())
                throw new AuthorizeFlowException("Bad Client", "Please provide a valid client");

            return this.redirectLoginPageIfNoAuth(loginPageInputMapper.fromAuthorizeDto(authorizeDto));

        } catch (AuthorizeFlowException e) {
            URI redirectTo = UriComponentsBuilder.fromUriString("/page/error")
                    .queryParam("title", e.getTitle())
                    .queryParam("description", e.getDescription())
                    .build().toUri();
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectTo.toString()).build();
        }
    }

    public Optional<ResponseEntity<Object>> redirectErrorIfBadClient(UUID clientId, String redirectUri) {
        URI redirectTo;
        Optional<Application> client = applicationService.getById(clientId);

        if (client.isEmpty()) {
            redirectTo = UriComponentsBuilder.fromUriString("/page/error")
                    .queryParam("title", "Invalid Client")
                    .queryParam("description", "Client could not found")
                    .build().toUri();
        } else if (!client.get().getRedirectUri().equals(redirectUri)) {
            redirectTo = UriComponentsBuilder.fromUriString("/page/error")
                    .queryParam("title", "Invalid Redirect URI")
                    .queryParam("description", "Unknown redirect location")
                    .build().toUri();
        } else
            return Optional.empty();
        return Optional.of(ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectTo.toString()).build());
    }

    public Optional<ResponseEntity<Object>> redirectErrorIfBadScope(String scope) {
        if (!oidcConfig.isScopeSupported(scope)) {
            URI redirectTo = UriComponentsBuilder.fromUriString("/page/error")
                    .queryParam("title", "Invalid Scope")
                    .queryParam("description", "The scope you provided is not supported by system")
                    .build().toUri();
            return Optional
                    .of(ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectTo.toString()).build());
        }
        return Optional.empty();
    }

    public ResponseEntity<Object> redirectLoginPageIfNoAuth(LoginPageInput loginPageInput) {

        Optional<Application> application = applicationService.getById(loginPageInput.getClientId());
        if (application.isEmpty())
            throw new RuntimeException("Client not found, should not happen due to previous checks");
        // Mapper denenebilir belki
        LoginSession loginSession = new LoginSession(
                application.get(), Set.of(loginPageInput.getScope().split(" ")),
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

    // TODO: Burası ciddili karıştı. Grant oluşturulması lazım, bu da UserLogin ile
    // oluşuyor. Bunun için de audience, scope değerleri veya LoginSession
    // gerekiyor. login session da neye göre oluşacak falan bissürü iş...
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
        userLogin.setLoggedUser(loggedUser.get());
        UserLogin savedUserLogin = userLoginService.save(userLogin);

        URI redirectTo = UriComponentsBuilder.fromUriString(loginSession.get().getRequestedClient().getRedirectUri())
                .queryParam("code", savedUserLogin.getAuthorizationGrant())
                .queryParam("state", loginSession.get().getState())
                .build().toUri();
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectTo.toString()).build();
    }
}
