package com.emakas.userService.service;

import com.emakas.userService.model.UserLogin;
import com.emakas.userService.repository.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserLoginService {
    private final UserLoginRepository userLoginRepository;

    @Autowired
    public UserLoginService(UserLoginRepository userLoginRepository) {
        this.userLoginRepository = userLoginRepository;
    }

    public Optional<UserLogin> saveUserLogin(UserLogin userLogin){
        try{
            return Optional.of(userLoginRepository.save(userLogin));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    public Optional<UserLogin> getUserLoginByGrant(String grant){
        UserLogin userLogin = userLoginRepository.findUserLoginByAuthorizationGrant(UUID.fromString(grant));
        Instant exprirationInstant = Instant.ofEpochSecond(userLogin.getExpirationDateInSeconds());
        Instant currentInstant = Instant.now();
        if (exprirationInstant.isBefore(currentInstant))
            return Optional.empty();
        return Optional.of(userLogin);
    }
}
