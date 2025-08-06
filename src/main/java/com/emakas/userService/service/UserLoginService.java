package com.emakas.userService.service;

import com.emakas.userService.model.UserLogin;
import com.emakas.userService.repository.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserLoginService extends CoreService<UserLogin, UUID> {
    private final UserLoginRepository userLoginRepository;

    @Autowired
    public UserLoginService(UserLoginRepository userLoginRepository) {
        super(userLoginRepository);
        this.userLoginRepository = userLoginRepository;
    }

    public Optional<UserLogin> getUserLoginByGrant(String grant){
        Optional<UserLogin> userLogin = userLoginRepository.findUserLoginByAuthorizationGrant(UUID.fromString(grant));
        if (userLogin.isEmpty())
            return Optional.empty();
        Instant exprirationInstant = Instant.ofEpochSecond(userLogin.get().getExpirationDateInSeconds());
        Instant currentInstant = Instant.now();
        if (exprirationInstant.isBefore(currentInstant))
            return Optional.empty();
        return Optional.of(userLogin.get());
    }
}
