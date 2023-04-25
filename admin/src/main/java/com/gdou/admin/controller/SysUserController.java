package com.gdou.admin.controller;


import com.gdou.common.annotaion.Log;
import com.gdou.common.domain.R;
import com.gdou.common.domain.entity.SysRole;
import com.gdou.common.domain.entity.SysUser;
import com.gdou.common.enums.BusinessType;
import com.gdou.common.utils.MapUtil;
import com.gdou.common.utils.SecurityUtils;
import com.gdou.common.utils.StringUtils;
import com.gdou.system.service.SysRoleService;
import com.gdou.system.service.SysUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.gdou.common.utils.SecurityUtils.getUserId;
import static com.gdou.common.utils.SecurityUtils.getUsername;

@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @GetMapping("/list")
    @PreAuthorize("@check.hasPermi('system:user:list')")
    public R list(@RequestParam Map<String, String> queryMap) {
        return R.success(userService.selectUserList(queryMap));
    }

    @GetMapping({"/", "/{userId}"})
    @PreAuthorize("@check.hasPermi('system:user:query')")
    public R getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        List<SysRole> roles = roleService.selectRoleAll();
        roles = SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList());
        var mapBuilder = MapUtil.builder()
                .put("roles", roles);
        if (StringUtils.isNotNull(userId)) {
            mapBuilder.put("user", userService.selectUserById(userId))
                    .put("roleIds", roleService.selectUserRoleIds(userId));
        }
        return R.success(mapBuilder.build());
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@check.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public R insertAuthRole(Long userId, Long[] roleIds) {
//        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return R.success("成功");
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@check.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public R resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
//        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return R.success(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@check.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(getUsername());
        return R.success(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@check.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public R authRole(@PathVariable("userId") Long userId) {
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        return R.success(MapUtil.builder()
                .put("user", user)
                .put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()))
                .build());
    }


    /**
     * 修改用户
     */
    @PreAuthorize("@check.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        if (!userService.checkUserNameUnique(user)) {
            return R.error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUsername());
        return R.success(userService.updateUser(user));
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@check.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody SysUser user) {
        if (!userService.checkUserNameUnique(user)) {
            return R.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return R.success(userService.insertUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@check.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, getUserId())) {
            return R.error("当前用户不能删除");
        }
        return R.success(userService.deleteUserByIds(userIds));
    }

}
