package com.emakas.userService.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import com.emakas.userService.model.*;
import com.emakas.userService.service.UserTokenService;
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

import com.emakas.userService.service.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final UserTokenService userTokenService;
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService service, UserTokenService userTokenService, AuthenticationManager authenticationManager, TokenManager tokenManager, PasswordEncoder passwordEncoder) {
        this.userService = service;
        this.userTokenService = userTokenService;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("sign-up")
    @ResponseBody
    public ResponseEntity<User> createUser(@RequestBody UserRegistrationDto userDto){
        if (this.userService.existsByEmailOrUserName(userDto.getEmail(), userDto.getUserName()))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        User user = new User(
                userDto.getUserName(),
                userDto.getEmail(),
                hashedPassword,
                userDto.getName(),
                userDto.getSurname()
        );
        userService.save(user);
    	return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("sign-in")
    @ResponseBody
    public ResponseEntity<Response<TokenResponse>> signIn(@RequestBody LoginModel loginModel){
        try{
            UserDetails userDetails = this.userService.loadUserByUsername(loginModel.getUname());

            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    loginModel.getPassword()
            ));
            if (auth.isAuthenticated()){
                User user = this.userService.getByUserName(loginModel.getUname());
                UserToken userToken = tokenManager.createUserToken(
                        user, Instant.now().plus(1, ChronoUnit.HOURS).getEpochSecond()
                );
                TokenResponse tokenResponse = new TokenResponse(
                        user.getUserName(),
                        user.getName(),
                        user.getSurname(),
                        user.getEmail(),
                        userToken.getSerializedToken()
                );
                return new ResponseEntity<>(new Response<>(tokenResponse,"Login Success"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Response<>(null,"Invalid credentials"), HttpStatus.NOT_FOUND);
        }catch (UsernameNotFoundException | BadCredentialsException exception){
            return new ResponseEntity<>(new Response<>(null,"Invalid credentials"), HttpStatus.NOT_FOUND);
        }
        catch (DisabledException exception){
            return new ResponseEntity<>(new Response<>(null,"User is suspended"), HttpStatus.FORBIDDEN);
        }
    }

    //@PutMapping("user")

    @DeleteMapping("delete/{uuid}")
    @ResponseBody
    public ResponseEntity<User> deleteUser(@PathVariable UUID uuid) {
    	User user = userService.getById(uuid);
    	if (user == null)
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	userService.delete(user);
    	return new ResponseEntity<User>(user,HttpStatus.OK);
    }
    
    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers(){
    	return new ResponseEntity<>(userService.getAll(),HttpStatus.OK);
    }
}
