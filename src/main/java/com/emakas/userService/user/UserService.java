package com.emakas.userService.user;

import com.emakas.userService.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService extends BaseService<User, UUID> {

    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
    }

}
