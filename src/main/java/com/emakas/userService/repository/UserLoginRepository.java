package com.emakas.userService.repository;

import com.emakas.userService.model.UserLogin;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserLoginRepository extends BaseRepository<UserLogin, UUID> {
    UserLogin findUserLoginByAuthorizationGrant(UUID authorizationGrant);
}
