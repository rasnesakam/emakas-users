package com.emakas.userService.oauth;

import com.emakas.userService.repository.EntityRepository;
import com.emakas.userService.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserTokenService extends EntityService<UserToken, UUID> {
    @Autowired
    public UserTokenService(EntityRepository<UserToken, UUID> entityRepository) {
        super(entityRepository);
    }
}
