package com.emakas.userService.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<User> createUser(@RequestBody User user){
    	service.save(user);
    	return new ResponseEntity<User>(user, HttpStatus.CREATED);
    }
    
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
    	return new ResponseEntity<List<User>>(service.getAll(),HttpStatus.OK);
    }
}
