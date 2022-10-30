package com.emakas.userService.oauth;

import com.emakas.userService.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserTokenService extends BaseService<UserToken, UUID> {
    @Autowired
    public UserTokenService(UserTokenRepository repository) {
        super(repository);
    }
}
