package com.gdou.framework.security.handler;


import com.alibaba.fastjson2.JSON;

import com.gdou.common.domain.R;
import com.gdou.common.constant.Constants;
import com.gdou.common.domain.model.LoginUser;
import com.gdou.common.utils.MapUtil;
import com.gdou.common.utils.MessageUtils;
import com.gdou.common.utils.SecurityUtils;
import com.gdou.common.utils.ServletUtils;
import com.gdou.framework.factory.AsyncFactory;
import com.gdou.framework.security.manager.AsyncManager;
import com.gdou.framework.web.service.TokenService;
import com.gdou.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysUserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        //获取LoginUser
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //记录登录信息
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginUser.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        userService.recordLoginInfo(loginUser.getUserId());
        R r = R.success("登陆成功", MapUtil.builder()
                .put(Constants.TOKEN, tokenService.createToken(loginUser))
                .build(), true);//标记登录成功
        outputStream.write(JSON.toJSONBytes(r));
        outputStream.flush();
        outputStream.close();
    }
}
