package com.gdou.framework.security.password;


import com.gdou.common.domain.model.LoginUser;
import com.gdou.framework.web.service.SysLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordLoginProvider implements AuthenticationProvider {

    @Autowired
    private SysLoginService loginService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PasswordLoginToken passwordLoginToken = (PasswordLoginToken) authentication;
        String username = passwordLoginToken.getUsername();
        String password = passwordLoginToken.getPassword();

        LoginUser loginUser = null;
        try {
            loginUser = loginService.login(username, password);
        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage());
        }

        return new PasswordLoginToken(null, loginUser);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordLoginToken.class.isAssignableFrom(authentication);
    }
}
