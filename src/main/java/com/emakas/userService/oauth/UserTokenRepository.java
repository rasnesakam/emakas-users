package com.emakas.userService.oauth;

import com.emakas.userService.repository.EntityRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserTokenRepository extends EntityRepository<UserToken, UUID> {
}
