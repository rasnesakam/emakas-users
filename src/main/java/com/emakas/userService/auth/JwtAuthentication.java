package com.emakas.userService.auth;

import com.emakas.userService.model.Token;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.stream.Collectors;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final Token token;

    public JwtAuthentication(Token token) {
        super(token.getScope().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        this.token = token;
        setAuthenticated(true);
    }

    public Token getUserToken() {
        return token;
    }
    @Override
    public Object getCredentials() {
        return token.getSerializedToken();
    }

    @Override
    public Object getPrincipal() {
        return token.getSub();
    }

    @Override
    public String getName() {
        return token.getSub();
    }
}
