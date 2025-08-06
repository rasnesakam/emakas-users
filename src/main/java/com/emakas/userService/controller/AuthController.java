package com.emakas.userService.controller;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.emakas.userService.dto.LoginModel;
import com.emakas.userService.dto.LoginSessionDto;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.UserWriteDto;
import com.emakas.userService.mappers.LoginSessionDtoMapper;
import com.emakas.userService.mappers.UserDtoMapper;
import com.emakas.userService.model.*;
import com.emakas.userService.service.*;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserLoginService userLoginService;
    private final ResourcePermissionService resourcePermissionService;
    private final UserDtoMapper userDtoMapper;
    private final ApplicationService applicationService;
    private final LoginSessionService loginSessionService;
    private final LoginSessionDtoMapper loginSessionDtoMapper;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final LoginModel loginModel;

    @Autowired
    public AuthController(UserService service, AuthenticationManager authenticationManager, TokenManager tokenManager, PasswordEncoder passwordEncoder, UserLoginService userLoginService, ResourcePermissionService resourcePermissionService, UserDtoMapper userDtoMapper, ApplicationService applicationService, LoginSessionService loginSessionService, LoginSessionDtoMapper loginSessionDtoMapper, LoginSessionDtoMapper loginSessionDtoMapper1, LoginModel loginModel) {
        this.userService = service;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userLoginService = userLoginService;
        this.resourcePermissionService = resourcePermissionService;
        this.userDtoMapper = userDtoMapper;
        this.applicationService = applicationService;
        this.loginSessionService = loginSessionService;
        this.loginSessionDtoMapper = loginSessionDtoMapper1;
        this.loginModel = loginModel;
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
     * @see AuthController#signIn(LoginModel, UUID, String[], String[], String)
     */
    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestParam(name = "client_id") UUID clientId,
            @RequestParam(name = "session_id") UUID sessionId,
            @RequestParam(name = "redirect_uri") String redirectUri,
            @RequestParam(name = "requested_scopes") String[] grantedScopes,
            @RequestParam(name = "state") String state,
            @RequestParam(name = "code_challenge", required = false) String codeChallenge
    ) {
        return applicationService.getById(clientId).map(
                client -> loginSessionService.getById(sessionId).map(loginSession -> {
                    if (Instant.ofEpochSecond(loginSession.getExpireDate()).isBefore(Instant.now()))
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Session Expired");
                    User user = loginSession.getIntendedUser();
                    Set<String> scopeSet = Set.of(grantedScopes);
                    //TODO: Compare requested scopes and authorizedScopes
                    UserLogin userLogin = new UserLogin(loginSession);
                    if (Objects.nonNull(codeChallenge) && !codeChallenge.isEmpty())
                        userLogin.setCodeChallenge(codeChallenge);
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
     *     Checks user credentials and permissions for user
     * </p>
     * <p>
     *     After filling login form, this endpoint is going to be invoke.<br/>
     *     This endpoint validates client and user credentials. After validation, requested permissions and audiences will be saved for authorizing login.<br/>
     *     For successful case, this enpoint returns {@link LoginSessionDto} that contains sessionId
     * </p>
     * <p>
     *     After signing-in, The method {@link AuthController#authorize(UUID, UUID, String, String[], String, String)} should invoke with received session id.
     * </p>
     * @param loginModel User credential informations that passed into body as form data
     * @param clientId Id that represents third party app client
     * @param audiences Indicates that wich resource should token use for
     * @param scopes Indicates the permissions that user has capable of
     * @param state State is a optional parameter that confirms the OAuth flow did not intercept by any other parties. According to RFC of OAuth2.0
     * @return {@link LoginSessionDto} that contains sessionId
     */
    @PostMapping("/sign-in")
    @ResponseBody
    public ResponseEntity<Response<LoginSessionDto>> signIn(@RequestBody LoginModel loginModel, @RequestParam(name = "client_id") UUID clientId, @RequestParam(required = false) String[] audiences, @RequestParam(required = false) String[] scopes, @RequestParam(required = false, name = "state") String state){
        return applicationService.getById(clientId).map(client -> {
            UserDetails userDetails = userService.loadUserByUsername(loginModel.getUsername());
            try{
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginModel.getUsername(), loginModel.getPassword()));
                if (!authentication.isAuthenticated())
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.<LoginSessionDto>of("Invalid credentials"));
                return userService.getByUserName(userDetails.getUsername()).map(user -> {
                    Set<String> audienceSet = Set.of(audiences);
                    Set<String> scopeSet = Set.of(scopes);
                    LoginSession loginSession = new LoginSession(user, client, scopeSet);
                    try {
                        LoginSession savedSession = loginSessionService.save(loginSession);
                        LoginSessionDto savedSessionResponse = loginSessionDtoMapper.toLoginSessionDto(savedSession);
                        savedSessionResponse.setState(state);
                        return ResponseEntity.status(HttpStatus.OK).body(Response.of(savedSessionResponse));
                    }catch (Exception e) {
                        logger.error(e.getLocalizedMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.<LoginSessionDto>of("Internal Error."));
                    }
                }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.of("User not found")));
            }
            catch (UsernameNotFoundException | BadCredentialsException exception){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.<LoginSessionDto>of(null,"Invalid credentials"));
            }
            catch (DisabledException exception){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Response.<LoginSessionDto>of(null,"User is suspended"));
            }
        }).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Response.of("Invalid client")));
    }

    private Set<String> getDefinedOrDefaultScopes(String[] scopes, User user){
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
