package com.emakas.userService.controller;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.emakas.userService.dto.LoginModel;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.UserRegistrationReadDto;
import com.emakas.userService.dto.UserWriteDto;
import com.emakas.userService.model.*;
import com.emakas.userService.service.ResourcePermissionService;
import com.emakas.userService.service.UserLoginService;
import com.emakas.userService.service.UserTokenService;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.Scope;
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

import com.emakas.userService.service.UserService;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final UserService userService;
    private final UserTokenService userTokenService;
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final PasswordEncoder passwordEncoder;
    private final UserLoginService userLoginService;
    private final ResourcePermissionService resourcePermissionService;

    @Autowired
    public AuthController(UserService service, UserTokenService userTokenService, AuthenticationManager authenticationManager, TokenManager tokenManager, PasswordEncoder passwordEncoder, UserLoginService userLoginService, ResourcePermissionService resourcePermissionService) {
        this.userService = service;
        this.userTokenService = userTokenService;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.passwordEncoder = passwordEncoder;
        this.userLoginService = userLoginService;
        this.resourcePermissionService = resourcePermissionService;
    }


    @PostMapping("sign-up")
    @ResponseBody
    public ResponseEntity<Response<User>> createUser(@RequestBody UserWriteDto userDto, @RequestParam(value = "invite-code") String inviteCode){
        if (this.userService.existsByEmailOrUserName(userDto.getEmail(), userDto.getUserName()))
            return new ResponseEntity<>(new Response<>(null, String.format("User %s (%s) already exists", userDto.getUserName(), userDto.getEmail())), HttpStatus.BAD_REQUEST);
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = new User(
                userDto.getUserName(),
                userDto.getEmail(),
                hashedPassword,
                userDto.getName(),
                userDto.getSurname()
        );
        userService.save(user);
    	return new ResponseEntity<>(new Response<>(user), HttpStatus.CREATED);
    }

    @PostMapping("sign-in")
    @ResponseBody
    public ResponseEntity<Response<String>> signIn(@RequestBody LoginModel loginModel, @RequestParam String[] audiences, @RequestParam String[] scopes){
        try{
            UserDetails userDetails = this.userService.loadUserByUsername(loginModel.getUsername());

            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    loginModel.getPassword()
            ));
            if (auth.isAuthenticated()){
                User user = this.userService.getByUserName(loginModel.getUsername());
                Set<String> audienceSet = Set.of(audiences);
                Set<String> scopeSet = getDefinedOrDefaultScopes(scopes, user);

                UserLogin userLogin = new UserLogin(user, audienceSet,scopeSet);
                Optional<UserLogin> savedUserLogin = userLoginService.saveUserLogin(userLogin);
                return savedUserLogin.map(login -> new ResponseEntity<>(
                        new Response<>(login.getAuthorizationGrant().toString(), "Login Success"),
                        HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(new Response<>(null, "sign in failed due to server error"),
                                HttpStatus.INTERNAL_SERVER_ERROR));
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

    @DeleteMapping("delete/{uuid}")
    @ResponseBody
    public ResponseEntity<User> deleteUser(@PathVariable UUID uuid) {
    	User user = userService.getById(uuid);
    	if (user == null)
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	userService.delete(user);
    	return new ResponseEntity<>(user,HttpStatus.OK);
    }

}
