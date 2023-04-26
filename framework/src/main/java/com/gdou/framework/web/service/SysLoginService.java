package com.gdou.framework.web.service;

import com.gdou.common.constant.Constants;
import com.gdou.common.constant.UserConstants;
import com.gdou.common.domain.entity.SysUser;
import com.gdou.common.domain.model.LoginUser;
import com.gdou.common.enums.UserStatus;
import com.gdou.common.exception.user.UserNotExistsException;
import com.gdou.common.exception.user.UserPasswordNotMatchException;
import com.gdou.common.utils.MessageUtils;
import com.gdou.common.utils.StringUtils;
import com.gdou.framework.factory.AsyncFactory;
import com.gdou.framework.security.manager.AsyncManager;
import com.gdou.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SysLoginService {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private SysPermissionService permissionService;


    public LoginUser login(String username, String password) throws Exception{
        loginPreCheck(username,password);

        SysUser user = userService.selectUserByUserName(username);
        if (StringUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new BadCredentialsException("登录用户：" + username + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new BadCredentialsException("对不起，您的账号：" + username + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new BadCredentialsException("对不起，您的账号：" + username + " 已停用");
        }
        passwordService.validate(user, password);

        return createLoginUser(user);
    }

    public LoginUser createLoginUser(SysUser user) {
        return new LoginUser(user.getUserId(), user, permissionService.getMenuPermission(user));
    }


    /**
     * 登录前置校验
     *
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
    }
}
