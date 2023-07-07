package com.emakas.userService.controller;

import java.util.List;
import java.util.UUID;

import com.emakas.userService.model.UserRegistrationDto;
import com.emakas.userService.shared.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.emakas.userService.model.User;
import com.emakas.userService.service.UserService;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
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
        service.save(user);
    	return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("user")

    @DeleteMapping("delete/{uuid}")
    @ResponseBody
    public ResponseEntity<User> deleteUser(@PathVariable UUID uuid) {
    	User user = service.getById(uuid);
    	if (user == null)
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	service.delete(user);
    	return new ResponseEntity<User>(user,HttpStatus.OK);
    }
    
    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers(){
    	return new ResponseEntity<>(service.getAll(),HttpStatus.OK);
    }
}
