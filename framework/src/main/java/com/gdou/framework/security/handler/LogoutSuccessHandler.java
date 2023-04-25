package com.gdou.framework.security.handler;


import com.alibaba.fastjson2.JSON;
import com.gdou.common.constant.Constants;
import com.gdou.common.domain.R;
import com.gdou.common.domain.model.LoginUser;
import com.gdou.common.utils.ServletUtils;
import com.gdou.common.utils.StringUtils;
import com.gdou.framework.factory.AsyncFactory;
import com.gdou.framework.security.manager.AsyncManager;
import com.gdou.framework.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

    @Autowired
    private TokenService tokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String userName = loginUser.getUserName();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, "退出成功"));
        }
        ServletUtils.renderString(response, JSON.toJSONString(R.success("退出成功")));
    }

}
