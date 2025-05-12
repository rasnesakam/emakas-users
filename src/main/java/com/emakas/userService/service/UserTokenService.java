package com.emakas.userService.service;

import com.emakas.userService.model.User;
import com.emakas.userService.model.UserToken;
import com.emakas.userService.repository.UserTokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserTokenService extends  CoreService<UserToken, UUID> {

    @Autowired
    public UserTokenService(UserTokenRepository repository) {
        super(repository);
    }

}
