package com.gdou.admin.controller;

import com.gdou.common.domain.R;
import com.gdou.common.domain.entity.SysMenu;
import com.gdou.common.domain.entity.SysUser;
import com.gdou.common.utils.MapUtil;
import com.gdou.common.utils.SecurityUtils;
import com.gdou.framework.web.service.SysPermissionService;
import com.gdou.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class SysLoginController {

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public R getInfo() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        return R.success(MapUtil.builder()
                .put("user", user)
                .put("roles", roles)
                .put("permissions", permissions)
                .build());
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    public R getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return R.success(menuService.buildMenus(menus));
    }
}
