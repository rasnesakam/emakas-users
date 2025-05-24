package com.emakas.userService.service;

import com.emakas.userService.model.User;
import com.emakas.userService.model.UserToken;
import com.emakas.userService.repository.UserTokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserTokenService {
    private final UserTokenRepository userTokenRepository;
    @Autowired
    public UserTokenService(UserTokenRepository repository) {
        this.userTokenRepository = repository;
    }
    public UserToken save(UserToken userToken) {
        return userTokenRepository.save(userToken);
    }
}
