package com.emakas.userService.service;

import com.emakas.userService.model.User;
import com.emakas.userService.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService extends BaseService<User, UUID> {
	private UserRepository repository;
    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
        this.repository = repository;
    }
    
    public User getByUserName(String userName) {
    	return repository.getByUserName(userName);
    }

}
