package com.emakas.userService.shared;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.emakas.userService.model.UserToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.jaas.JaasGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthentication extends AbstractAuthenticationToken {
    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */

    private String issuer;
    private String subject;
    private JwtAuthentication(Collection<? extends GrantedAuthority> authorities, String subject, String issuer) {
        super(authorities);
    }

    public static JwtAuthentication getInstance(UserToken token){
        List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(token.getAud());
        String issuer = token.getIss();
        String subject = token.getSub();
        return new JwtAuthentication(authorityList, issuer, subject);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
