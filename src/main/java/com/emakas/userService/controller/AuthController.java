package com.emakas.userService.controller;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.emakas.userService.domain.auth.UserPrincipal;
import com.emakas.userService.dto.LoginSessionDto;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.UserWriteDto;
import com.emakas.userService.mappers.UserPrincipalMapper;
import com.emakas.userService.model.ResourcePermission;
import com.emakas.userService.model.User;
import com.emakas.userService.service.ApplicationService;
import com.emakas.userService.service.LoginSessionService;
import com.emakas.userService.service.ResourcePermissionService;
import com.emakas.userService.service.TokenService;
import com.emakas.userService.service.UserLoginService;
import com.emakas.userService.service.UserService;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.converters.StringToCodeChallengeMethodConverter;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final TokenService tokenService;
    private final UserPrincipalMapper userPrincipalMapper;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager,
            ResourcePermissionService resourcePermissionService, TokenService tokenService,
            UserPrincipalMapper userPrincipalMapper) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userPrincipalMapper = userPrincipalMapper;
    }

    @PostMapping("/sign-up")
    @ResponseBody
    public ResponseEntity<Response<User>> createUser(@RequestBody UserWriteDto userDto,
            @RequestParam(value = "invite-code") String inviteCode) {
        // TODO: Signing up feature will design again
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * <h1>Sign-In</h1>
     * <p>
     * Checks user credentials for user
     * </p>
     * <p>
     * Login page posts a request to this endpoint to validate user.<br/>
     * If validation successfull, this endpoint will redirect user to
     * /oauth2/authorize after creation of session for logged in user<br/>
     * </p>
     * 
     * @param username:    username value of user attended to log in
     * @param password:    password value of user attended to log in
     * @param continueUri: URI that will be redirected after operation
     * @return {@link LoginSessionDto} that contains sessionId
     */
    @PostMapping("/sign-in")
    @ResponseBody
    public ResponseEntity<?> signIn(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam("continue_uri") String continueUri,
            HttpServletRequest request,
            HttpServletResponse response) {
        logger.info(String.format("User '%s' wants to login and continue to uri: %s", username, continueUri));
        URI redirectUri;
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (!authentication.isAuthenticated())
                throw new BadCredentialsException("");
            redirectUri = UriComponentsBuilder.fromUriString(URLDecoder.decode(continueUri, Charset.defaultCharset()))
                    .build().toUri();
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
        } catch (UsernameNotFoundException | BadCredentialsException | DisabledException exception) {
            logger.error(
                    String.format("User '%s' failed to login. Error message: %s", username, exception.getMessage()));
            redirectUri = UriComponentsBuilder.fromUriString("/page/auth/login")
                    .queryParam("error", exception.getMessage())
                    .queryParam("continue", continueUri)
                    .build()
                    .toUri();
        }
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUri.toString()).build();
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
