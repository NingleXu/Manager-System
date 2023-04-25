package com.gdou.framework.security.password;

import com.alibaba.fastjson2.JSON;
import com.gdou.common.domain.model.LoginBody;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.gdou.common.constant.Constants.HTTP_METHOD_POST;
import static com.gdou.common.constant.UrlConstants.LOGIN_URL;


public class PasswordLoginFilter extends AbstractAuthenticationProcessingFilter {
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher(LOGIN_URL, HTTP_METHOD_POST);

    public PasswordLoginFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //进入登入验证
        //请求方法校验
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        //获取用户名和密码
        LoginBody loginBody = JSON.parseObject(request.getInputStream(), LoginBody.class);
        PasswordLoginToken token = new PasswordLoginToken(null,
                loginBody.getUsername(), loginBody.getPassword());
        //存入detail中
        setDetails(request, token);

        //验证用户名和密码是否正确
        return this.getAuthenticationManager().authenticate(token);
    }

    protected void setDetails(HttpServletRequest request, PasswordLoginToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

}
