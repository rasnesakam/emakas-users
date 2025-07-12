package com.emakas.userService.service;

import com.emakas.userService.model.LoginSession;
import com.emakas.userService.repository.CoreRepository;
import com.emakas.userService.repository.LoginSessionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoginSessionService extends CoreService<LoginSession, UUID> {
    private LoginSessionRepository loginSessionRepository;
    public LoginSessionService(LoginSessionRepository loginSessionRepository) {
        super(loginSessionRepository);
        this.loginSessionRepository = loginSessionRepository;
    }
}
