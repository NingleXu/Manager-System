package com.gdou.system.service;

import com.gdou.common.domain.TreeSelect;
import com.gdou.common.domain.entity.SysMenu;
import com.gdou.system.domain.RouterVo;

import java.util.Collection;
import java.util.List;


public interface SysMenuService {


    List<SysMenu> selectMenuList(Long userId);

    Collection<String> selectMenuPermsByUserId(Long userId);

    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<SysMenu> menus);

    List<SysMenu> selectMenuList(SysMenu menu,Long userId);

    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    List<Long> selectMenuListByRoleId(Long roleId);

    List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    boolean hasChildByMenuId(Long menuId);

    boolean checkMenuExistRole(Long menuId);

    int deleteMenuById(Long menuId);

    boolean checkMenuNameUnique(SysMenu menu);

    int updateMenu(SysMenu menu);

    SysMenu selectMenuById(Long menuId);

    int insertMenu(SysMenu menu);
}
