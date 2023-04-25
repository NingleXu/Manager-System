package com.gdou.framework.security.handler;

import com.alibaba.fastjson2.JSON;
import com.gdou.common.domain.R;
import com.gdou.common.constant.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class LoginFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();
        R r = R.error(exception.getMessage(), HttpStatus.ERROR);//标记登录失败
        outputStream.write(JSON.toJSONBytes(r));
        outputStream.flush();
        outputStream.close();
        ;
    }
}
