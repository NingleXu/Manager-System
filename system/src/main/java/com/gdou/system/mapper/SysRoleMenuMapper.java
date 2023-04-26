package com.gdou.system.mapper;


import com.gdou.common.domain.entity.SysRoleMenu;

import java.util.List;


public interface SysRoleMenuMapper {
    int batchRoleMenu(List<SysRoleMenu> list);

    int deleteRoleMenuByRoleId(Long roleId);

    int deleteRoleMenu(Long[] roleIds);

    int checkMenuExistRole(Long menuId);
}
