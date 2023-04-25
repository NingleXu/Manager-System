package com.gdou.admin.controller;


import com.gdou.common.annotaion.Log;
import com.gdou.common.constant.UserConstants;
import com.gdou.common.domain.R;
import com.gdou.common.domain.TreeSelect;
import com.gdou.common.domain.entity.SysMenu;
import com.gdou.common.enums.BusinessType;
import com.gdou.common.utils.MapUtil;
import com.gdou.common.utils.StringUtils;
import com.gdou.system.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gdou.common.utils.SecurityUtils.getUserId;
import static com.gdou.common.utils.SecurityUtils.getUsername;

@RestController
@RequestMapping("/system/menu")
public class SysMenuController {


    @Autowired
    private SysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@check.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public R list(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return R.success(menus);
    }


    /**
     * @author xzh
     * @time 2023/4/12 10:10
     * 查询一个角色菜单树形结构
     */
    @GetMapping("/roleMenuTreeselect/{roleId}")
    public R roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        //查看当前角色能看到的菜单
        List<SysMenu> menus = menuService.selectMenuList(getUserId());
        //查看角色所能看到的菜单
        List<Long> roleMenusId = menuService.selectMenuListByRoleId(roleId);

        return R.success(MapUtil.builder()
                .put("checkedKeys", roleMenusId)
                .put("menus", menuService.buildMenuTreeSelect(menus))
                .build());
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public R treeselect() {
        List<SysMenu> menus = menuService.selectMenuList(getUserId());
        return R.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@check.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public R getInfo(@PathVariable Long menuId) {
        return R.success(menuService.selectMenuById(menuId));
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@check.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return R.error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getMenuId().equals(menu.getParentId())) {
            return R.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(getUsername());
        return R.success(menuService.updateMenu(menu));
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@check.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            return R.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(getUsername());
        return R.success(menuService.insertMenu(menu));
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    @PreAuthorize("@check.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    public R remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return R.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return R.error("菜单已分配,不允许删除");
        }
        return R.success(menuService.deleteMenuById(menuId));
    }


}
