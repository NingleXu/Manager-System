package com.gdou.system.mapper;

import com.gdou.common.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface SysMenuMapper {
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeAll();

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据用户查询系统菜单列表
     * @param menu
     * @return菜单列表
     */
    List<SysMenu> selectMenuListByUserId(SysMenu menu);

    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);

    List<SysMenu> selectMenuList(SysMenu menu);

    SysMenu selectMenuById(Long menuId);

    int updateMenu(SysMenu menu);

    int insertMenu(SysMenu menu);

    SysMenu checkMenuNameUnique(SysMenu menu);

    int hasChildByMenuId(Long menuId);

    int deleteMenuById(Long menuId);
}
