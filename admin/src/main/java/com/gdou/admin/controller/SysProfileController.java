package com.gdou.admin.controller;


import com.gdou.common.annotaion.Log;
import com.gdou.common.domain.R;
import com.gdou.common.domain.entity.SysUser;
import com.gdou.common.domain.model.LoginUser;
import com.gdou.common.enums.BusinessType;
import com.gdou.common.utils.MapUtil;
import com.gdou.common.utils.SecurityUtils;
import com.gdou.common.utils.StringUtils;
import com.gdou.framework.web.service.TokenService;
import com.gdou.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.gdou.common.utils.SecurityUtils.getLoginUser;


@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private TokenService tokenService;


    /**
     * @author xzh
     * @time 2023/4/8 17:09
     * 个人信息
     */
    @GetMapping
    public R profile() {
        LoginUser loginUser = getLoginUser();
        //查询用户角色
        return R.success(MapUtil.builder()
                .put("user", loginUser.getUser())
                .put("roleGroup", userService.selectUserRoleGroup(loginUser.getUserName()))
                .build());
    }

    /**
     * @author xzh
     * @time 2023/4/26 17:07
     * 修改个人信息
     */

    @PutMapping
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    public R updateProfile(@RequestBody SysUser user) {
        LoginUser loginUser = getLoginUser();
        SysUser sysUser = loginUser.getUser();
        user.setUserName(sysUser.getUserName());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUserId(sysUser.getUserId());
        user.setPassword(null);
        user.setAvatar(null);
        if (userService.updateUserProfile(user) > 0) {
            // 更新缓存用户信息
            sysUser.setNickName(user.getNickName());
            sysUser.setPhonenumber(user.getPhonenumber());
            sysUser.setEmail(user.getEmail());
            sysUser.setSex(user.getSex());
            tokenService.setLoginUser(loginUser);
            return R.success("更新成功！");
        }
        return R.error("修改个人信息异常，请联系管理员");
    }


    /**
     * 重置密码
     */

    @PutMapping("/updatePwd")
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    public R updatePwd(String oldPassword, String newPassword) {
        LoginUser loginUser = getLoginUser();
        String userName = loginUser.getUserName();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            return R.error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return R.error("新密码不能与旧密码相同");
        }
        if (userService.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)) > 0) {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            tokenService.setLoginUser(loginUser);
            return R.success("修改成功！");
        }
        return R.error("修改密码异常，请联系管理员");
    }
}
