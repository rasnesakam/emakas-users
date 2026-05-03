package com.emakas.userService.auth;

import com.emakas.userService.domain.auth.UserPrincipal;
import com.emakas.userService.model.Token;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.stream.Collectors;

public class JwtAuthentication extends AbstractAuthenticationToken {
    //TODO:Change Token based structure to UserPrincipal Structure. 4 Occurences detected. TokenPermissionEvaluator (1), SecurityContextManager (1), TeamsController(2)
    private final UserPrincipal userPrincipal;

    @Deprecated
    public JwtAuthentication(Token token) {
        super(token.getScope().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        this.userPrincipal = null;
        setAuthenticated(false);
    }

    public JwtAuthentication(UserPrincipal userPrincipal) {
        super(userPrincipal.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        this.userPrincipal = userPrincipal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.userPrincipal;
    }

}
