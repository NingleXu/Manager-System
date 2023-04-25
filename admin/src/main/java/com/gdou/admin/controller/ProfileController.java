package com.gdou.admin.controller;


import com.gdou.common.domain.R;
import com.gdou.common.domain.model.LoginUser;
import com.gdou.common.utils.MapUtil;
import com.gdou.common.utils.SecurityUtils;
import com.gdou.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/system/user/profile")
public class ProfileController {

    @Autowired
    private SysUserService userService;


    /**
     * @author xzh
     * @time 2023/4/8 17:09
     * 个人信息
     */
    @GetMapping
    public R profile() {
        LoginUser loginUser = SecurityUtils.getLoginUser();

        //查询用户角色
        return R.success(MapUtil.builder()
                .put("user", loginUser.getUser())
                .put("roleGroup", userService.selectUserRoleGroup(loginUser.getUserName()))
                .build());
    }

}
