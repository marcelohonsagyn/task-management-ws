package br.com.marcelohonsa.taskmanager.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtAuthentication extends AbstractAuthenticationToken {
    private final UserDetails principal;
    private final String token;

    public JwtAuthentication(UserDetails principal, String token) {
        super(null);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
