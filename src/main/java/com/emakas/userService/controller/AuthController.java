package com.emakas.userService.controller;

import java.time.Instant;
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

    @Autowired
    public AuthController(UserService service, TokenService tokenService, AuthenticationManager authenticationManager, TokenManager tokenManager, PasswordEncoder passwordEncoder, UserLoginService userLoginService, ResourcePermissionService resourcePermissionService, UserDtoMapper userDtoMapper, ApplicationService applicationService, LoginSessionService loginSessionService, LoginSessionDtoMapper loginSessionDtoMapper, LoginSessionDtoMapper loginSessionDtoMapper1) {
        this.userService = service;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userLoginService = userLoginService;
        this.resourcePermissionService = resourcePermissionService;
        this.userDtoMapper = userDtoMapper;
        this.applicationService = applicationService;
        this.loginSessionService = loginSessionService;
        this.loginSessionDtoMapper = loginSessionDtoMapper1;
    }


    @PostMapping("/sign-up")
    @ResponseBody
    public ResponseEntity<Response<User>> createUser(@RequestBody UserWriteDto userDto, @RequestParam(value = "invite-code") String inviteCode){
        if (this.userService.existsByEmailOrUserName(userDto.getEmail(), userDto.getUserName()))
            return new ResponseEntity<>(new Response<>(null, String.format("User %s (%s) already exists", userDto.getUserName(), userDto.getEmail())), HttpStatus.BAD_REQUEST);
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = userDtoMapper.UserFromUserWriteDto(userDto);
        user.setPassword(hashedPassword);
        userService.save(user);
    	return new ResponseEntity<>(new Response<>(user), HttpStatus.CREATED);
    }

    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestParam(name = "client_id") UUID clientId,
            @RequestParam(name = "session_id") UUID sessionId,
            @RequestParam(name = "redirect_uri") String redirectUri,
            @RequestParam(name = "requested_scopes") String[] requestedScopes,
            @RequestParam(name = "state") String state
    ) {
        Optional<Application> optionalClient = applicationService.getById(clientId);
        if (optionalClient.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        Application client = optionalClient.get();

        Optional<LoginSession> optionalLoginSession = loginSessionService.getById(sessionId);
        if (optionalLoginSession.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");

        LoginSession loginSession = optionalLoginSession.get();
        if (Instant.ofEpochSecond(loginSession.getExpireDate()).isBefore(Instant.now()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Session Expired");

        User user = loginSession.getIntendedUser();

        Set<String> audienceSet = Set.of(client.getUri());
        Set<String> scopeSet = Set.of(requestedScopes);

        UserLogin userLogin = new UserLogin(user, audienceSet,scopeSet);
        Optional<UserLogin> savedUserLogin = userLoginService.saveUserLogin(userLogin);

        if (savedUserLogin.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error.");

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(client.getRedirectUri())
                .queryParam("code",savedUserLogin.get().getAuthorizationGrant())
                .queryParam("state", state);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(uriBuilder.build().toUri())
                .build();
    }

    @PostMapping("/sign-in")
    @ResponseBody
    public ResponseEntity<Response<LoginSessionDto>> signIn(@RequestBody LoginModel loginModel, @RequestParam UUID clientId, @RequestParam String[] audiences, @RequestParam String[] scopes){
        try{
            Optional<Application> clientOptional = applicationService.getById(clientId);
            if (clientOptional.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.of("Invalid client"));

            UserDetails userDetails = this.userService.loadUserByUsername(loginModel.getUsername());
            Application client = clientOptional.get();
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    loginModel.getPassword()
            ));
            if (auth.isAuthenticated()){
                User user = this.userService.getByUserName(loginModel.getUsername()).get();
                Set<String> audienceSet = Set.of(audiences);
                Set<String> scopeSet = getDefinedOrDefaultScopes(scopes, user);

                UserLogin userLogin = new UserLogin(user, audienceSet,scopeSet);

                LoginSession loginSession = new LoginSession();
                loginSession.setIntendedUser(user);
                loginSession.setRequestedClient(client);
                loginSession.setRedirectUri(client.getRedirectUri());
                loginSession.setRequestedScopes(scopeSet);

                LoginSessionDto loginSessionResponse = loginSessionDtoMapper.toLoginSessionDto(loginSessionService.save(loginSession));

                Optional<UserLogin> savedUserLogin = userLoginService.saveUserLogin(userLogin);
                return savedUserLogin.map(login -> ResponseEntity.status(HttpStatus.CREATED).body(Response.of(loginSessionResponse)))
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.of("sign in failed due to server error")));
            }
            return new ResponseEntity<>(new Response<>(null,"Invalid credentials"), HttpStatus.NOT_FOUND);
        }catch (UsernameNotFoundException | BadCredentialsException exception){
            return new ResponseEntity<>(new Response<>(null,"Invalid credentials"), HttpStatus.NOT_FOUND);
        }
        catch (DisabledException exception){
            return new ResponseEntity<>(new Response<>(null,"User is suspended"), HttpStatus.FORBIDDEN);
        }
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
