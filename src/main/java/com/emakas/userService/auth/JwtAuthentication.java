package com.emakas.userService.auth;

import com.emakas.userService.model.UserToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.stream.Collectors;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final UserToken userToken;

    public JwtAuthentication(UserToken userToken) {
        super(userToken.getScope().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        this.userToken = userToken;
        setAuthenticated(true);
    }

    public UserToken getUserToken() {
        return userToken;
    }
    @Override
    public Object getCredentials() {
        return userToken.getSerializedToken();
    }

    @Override
    public Object getPrincipal() {
        return userToken.getSub();
    }

    @Override
    public String getName() {
        return userToken.getSub();
    }
}
