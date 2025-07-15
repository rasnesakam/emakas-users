package com.emakas.userService.repository;

import com.emakas.userService.model.UserLogin;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserLoginRepository extends CoreRepository<UserLogin, UUID> {
    Optional<UserLogin> findUserLoginByAuthorizationGrant(UUID authorizationGrant);
}
