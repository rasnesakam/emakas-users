package com.emakas.userService.controller;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.emakas.userService.domain.auth.UserPrincipal;
import com.emakas.userService.dto.LoginModel;
import com.emakas.userService.dto.LoginSessionDto;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.UserWriteDto;
import com.emakas.userService.mappers.LoginSessionDtoMapper;
import com.emakas.userService.mappers.UserDtoMapper;
import com.emakas.userService.mappers.UserPrincipalMapper;
import com.emakas.userService.model.*;
import com.emakas.userService.service.*;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.converters.StringToCodeChallengeMethodConverter;
import com.emakas.userService.shared.enums.CodeChallengeMethod;
import com.emakas.userService.shared.enums.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserLoginService userLoginService;
    private final ResourcePermissionService resourcePermissionService;
    private final ApplicationService applicationService;
    private final LoginSessionService loginSessionService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final StringToCodeChallengeMethodConverter stringToCodeChallengeMethodConverter;
    private final TokenService tokenService;
    private final String appDomainName;
    private final UserPrincipalMapper userPrincipalMapper;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager, UserLoginService userLoginService, ResourcePermissionService resourcePermissionService, ApplicationService applicationService, LoginSessionService loginSessionService, StringToCodeChallengeMethodConverter stringToCodeChallengeMethodConverter, TokenService tokenService, @Value("${app.domain}") String appDomainName, UserPrincipalMapper userPrincipalMapper) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userLoginService = userLoginService;
        this.resourcePermissionService = resourcePermissionService;
        this.applicationService = applicationService;
        this.loginSessionService = loginSessionService;
        this.stringToCodeChallengeMethodConverter = stringToCodeChallengeMethodConverter;
        this.tokenService = tokenService;
        this.appDomainName = appDomainName;
        this.userPrincipalMapper = userPrincipalMapper;
    }


    @PostMapping("/sign-up")
    @ResponseBody
    public ResponseEntity<Response<User>> createUser(@RequestBody UserWriteDto userDto, @RequestParam(value = "invite-code") String inviteCode){
        //TODO: Signing up feature will design again
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * <h1>Authorize</h1>
     * <p>Redirect to third party app with approved credentials.</p>
     * <p>
     *     This method should not use alone
     * </p>
     * <p>
     *     This method should use after invoke <code>/sign-in</code> endpoint
     *     with <code>sessionId</code> parameter that returned from the method
     * </p>
     *
     * @param clientId The id of the client app that registered
     * @param sessionId After signing in, the sessionId will generate in order to represent that login.
     * @param redirectUri The uri of the client app that registered. After authorize,
     * @param grantedScopes The client may request some permissions. But client can approve some, none or all of them.
     * @param state State is a optional parameter that confirms the OAuth flow did not intercept by any other parties. According to RFC of OAuth2.0
     * @return For Successful process, method returns redirect response to the client callback uri
     *
     */
    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestParam(name = "client_id") UUID clientId,
            @RequestParam(name = "session_id") UUID sessionId,
            @RequestParam(name = "redirect_uri") String redirectUri,
            @RequestParam(name = "granted_scopes") String[] grantedScopes,
            @RequestParam(name = "state") String state,
            @RequestParam(name = "code_challenge", required = false) String codeChallenge,
            @RequestParam(name = "code_challenge_method", required = false) String codeChallengeMethodString
            ) {
        return applicationService.getById(clientId).map(
                client -> loginSessionService.getById(sessionId).map(loginSession -> {
                    if (Instant.now().isAfter(Instant.ofEpochSecond(loginSession.getExpireDate())))
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Session Expired");
                    //User user = loginSession.getIntendedUser();
                    Set<String> scopeSet = Set.of(grantedScopes);
                    //TODO: Compare requested scopes and authorizedScopes
                    UserLogin userLogin = new UserLogin(loginSession);
                    if (Objects.nonNull(codeChallenge) && !codeChallenge.isEmpty()){
                        userLogin.setCodeChallenge(codeChallenge);
                        userLogin.setCodeChallengeMethod(stringToCodeChallengeMethodConverter.convert(codeChallengeMethodString));
                    }
                    try {
                        UserLogin savedUserLogin = userLoginService.save(userLogin);
                        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromUriString(client.getRedirectUri())
                                .queryParam("code",savedUserLogin.getAuthorizationGrant())
                                .queryParam("state", state);
                        return ResponseEntity.status(HttpStatus.FOUND)
                                .location(uriBuilder.build().toUri())
                                .build();
                    }catch (Exception e) {
                        logger.error(e.getLocalizedMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error.");
                    }
                }).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
        ).orElse(ResponseEntity.notFound().build());
    }

    /**
     * <h1>Sign-In</h1>
     * <p>
     *     Checks user credentials for user
     * </p>
     * <p>
     *     Login page posts a request to this endpoint to validate user.<br/>
     *     If validation successfull, this endpoint will redirect user to /oauth2/authorize after creation of session for logged in user<br/>
     * </p>
     * @param username:  username value of user attended to log in
     * @param password:  password value of user attended to log in
     * @param continueUri:  URI that will be redirected after operation
     * @return {@link LoginSessionDto} that contains sessionId
     */
    @PostMapping("/sign-in")
    @ResponseBody
    public ResponseEntity<?> signIn(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam("continue_uri") String continueUri,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        URI redirectUri;
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (!authentication.isAuthenticated())
                throw new BadCredentialsException("");
            redirectUri = UriComponentsBuilder.fromUriString(URLDecoder.decode(continueUri, Charset.defaultCharset())).build().toUri();
            Optional<User> authenticatedUser = userService.getByUserName(username);
            if (authenticatedUser.isEmpty())
                throw new UsernameNotFoundException("Could not proceed with that user. Please try again later.");
            User user = authenticatedUser.get();
            UserPrincipal userPrincipal = userPrincipalMapper.fromUser(user);
            String token = tokenService.createSignInToken(userPrincipal, request);
            ResponseCookie responseCookie = ResponseCookie.from(Constants.AUTH_COOKIE_PARAMETER, token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofMinutes(10))
                    .sameSite("Lax")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        }
        catch (UsernameNotFoundException | BadCredentialsException | DisabledException exception){
            redirectUri = UriComponentsBuilder.fromUriString("/page/auth/login")
                    .queryParam("error", exception.getMessage())
                    .queryParam("continue", continueUri)
                    .build()
                    .toUri();
        }
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUri.toString()).build();
    }

    private Set<String> getDefinedOrDefaultScopesForApp(String[] scopes, User user){
        return scopes.length > 0 ? Set.of(scopes) :
                resourcePermissionService.getPermissionsByUser(user)
                        .stream().map(ResourcePermission::toString).collect(Collectors.toSet());
    }

    private Set<String> getDefinedOrDefaultAudiences(String[] audiences, User user){
        return audiences.length > 0 ? Set.of(audiences) :
                resourcePermissionService.getPermissionsByUser(user)
                        .stream().map(ResourcePermission::toString).collect(Collectors.toSet());
    }

    @DeleteMapping("/delete/{uuid}")
    @ResponseBody
    public ResponseEntity<Response<User>> deleteUser(@PathVariable UUID uuid) {
    	Optional<User> user = userService.getById(uuid);
    	if (user.isEmpty())
    		return ResponseEntity.notFound().build();
    	userService.delete(user.get());
    	return ResponseEntity.ok(Response.of(user.get(), "User deleted"));
    }

}
