package com.gdou.framework.security.password;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class PasswordLoginToken extends AbstractAuthenticationToken {

    private String username;

    private String password;

    private Object principal;

    public PasswordLoginToken(Collection<? extends GrantedAuthority> authorities, Object principal) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }
    public PasswordLoginToken(Collection<? extends GrantedAuthority> authorities, String username, String password) {
        super(authorities);
        this.username = username;
        this.password = password;
        super.setAuthenticated(false);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
