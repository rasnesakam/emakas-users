package com.emakas.userService.shared;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.dto.Response;
import com.emakas.userService.model.Tenant;
import com.emakas.userService.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityContextManager {

    private final TokenManager tokenManager;

    public SecurityContextManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public Optional<Tenant> getCurrentTenant() {
        Optional<User> currentUser = this.getCurrentUser();
        return currentUser.map(User::getTenant);
    }

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return Optional.empty();
        if (authentication instanceof JwtAuthentication jwtAuthentication) {
            return tokenManager.loadUserFromToken(jwtAuthentication.getUserToken());
        }
        return Optional.empty();
    }
}
