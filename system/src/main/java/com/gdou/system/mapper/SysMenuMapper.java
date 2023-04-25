package com.gdou.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdou.common.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface SysMenuMapper extends BaseMapper<SysMenu> {
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

}
