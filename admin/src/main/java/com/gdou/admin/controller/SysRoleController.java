package com.gdou.admin.controller;


import com.gdou.common.annotaion.Log;
import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.R;
import com.gdou.common.domain.entity.SysRole;
import com.gdou.common.domain.entity.SysUser;
import com.gdou.common.domain.entity.SysUserRole;
import com.gdou.common.domain.model.LoginUser;
import com.gdou.common.enums.BusinessType;
import com.gdou.common.utils.StringUtils;
import com.gdou.framework.web.service.SysPermissionService;
import com.gdou.framework.web.service.TokenService;
import com.gdou.system.service.SysMenuService;
import com.gdou.system.service.SysRoleService;
import com.gdou.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.gdou.common.utils.SecurityUtils.getLoginUser;
import static com.gdou.common.utils.SecurityUtils.getUsername;

@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private SysUserService userService;

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @GetMapping("/list")
    @PreAuthorize("@check.hasPermi('system:role:list')")
    public R list(@RequestParam Map<String, String> queryMap) {
        return R.success(roleService.selectRoleList(queryMap));
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@check.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public R getInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(roleId);
        return R.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @PostMapping
    @PreAuthorize("@check.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    public R add(@RequestBody SysRole role) {
        if (!roleService.checkRoleNameUnique(role)) {
            return R.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(getUsername());
        return R.success(roleService.insertRole(role));
    }

    /**
     * 查询已分配用户角色列表
     */
    @PreAuthorize("@check.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public R allocatedList(@RequestParam Map<String, String> queryMap) {
        return R.success(userService.selectAllocatedList(queryMap));
    }

    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@check.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public R unallocatedList(@RequestParam Map<String, String> queryMap) {
        return R.success(userService.selectUnallocatedList(queryMap));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@check.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(getUsername());
        return R.success(roleService.updateRoleStatus(role));
    }

    /**
     * 修改保存角色
     */
    @PutMapping
    @PreAuthorize("@check.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    public R edit(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (!roleService.checkRoleNameUnique(role)) {
            return R.error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(getUsername());

        if (roleService.updateRole(role) > 0) {
            // 更新缓存用户权限
            LoginUser loginUser = getLoginUser();
            //用户已经登录 并且不是 管理员
            if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin()) {
                //重新设置菜单权限
                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                //设置用户信息
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                //刷新缓存
                tokenService.setLoginUser(loginUser);
            }
            return R.success("成功！");
        }
        return R.error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }


    /**
     * 删除角色
     */
    @PreAuthorize("@check.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public R remove(@PathVariable Long[] roleIds) {
        return R.success(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 取消授权用户
     */
    @PreAuthorize("@check.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public R cancelAuthUser(@RequestBody SysUserRole userRole) {
        return R.success(roleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     */
    @PreAuthorize("@check.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public R cancelAuthUserAll(Long roleId, Long[] userIds) {
        return R.success(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@check.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public R selectAuthUserAll(Long roleId, Long[] userIds) {
        roleService.checkRoleDataScope(roleId);
        return R.success(roleService.insertAuthUsers(roleId, userIds));
    }

}
