package com.emakas.userService.controller;

import java.util.List;
import java.util.UUID;

import com.emakas.userService.model.LoginModel;
import com.emakas.userService.model.Response;
import com.emakas.userService.model.UserRegistrationDto;
import com.emakas.userService.shared.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.emakas.userService.model.User;
import com.emakas.userService.service.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    public UserController(UserService service, AuthenticationManager authenticationManager) {
        this.userService = service;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("sign-up")
    @ResponseBody
    public ResponseEntity<User> createUser(@RequestBody UserRegistrationDto userDto){
        String passwordSalt = AuthHelper.generateRandomPasswordSalt();
        String hashedPassword = AuthHelper.getHashedPassword(userDto.getPassword(), passwordSalt);
    	User user = new User(
                userDto.getUname(),
                userDto.getEmail(),
                hashedPassword,
                userDto.getName(),
                userDto.getSurname(),
                passwordSalt
        );
        userService.save(user);
    	return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("sign-in")
    @ResponseBody
    public ResponseEntity<Response<User>> signIn(@RequestBody LoginModel loginModel){
        try{
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginModel.getUname(),
                    loginModel.getPassword()
            ));
            if (auth.isAuthenticated()){
                User user = this.userService.getByUserName(loginModel.getUname());
                return new ResponseEntity<>(new Response<>(user), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(new Response<>(null,"Invalid credentials"), HttpStatus.NOT_FOUND);
            }
        }catch (DisabledException exception){
            return new ResponseEntity<>(new Response<>(null,"User is suspended"), HttpStatus.FORBIDDEN);
        }
        catch (BadCredentialsException exception){
            return new ResponseEntity<>(new Response<>(null,"Invalid credentials"), HttpStatus.NOT_FOUND);
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
