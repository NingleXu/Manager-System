package com.gdou.system.mapper;


import com.gdou.common.domain.entity.SysRole;

import java.util.List;


public interface SysRoleMapper {

    List<SysRole> selectRolePermissionByUserId(Long userId);

    List<SysRole> selectRolesByUsername(String username);

    List<SysRole> selectRoleList(SysRole role);

    int updateRole(SysRole role);

    int deleteRoleByIds(Long[] roleIds);

    SysRole checkRoleNameUnique(String roleName);

    SysRole checkRoleKeyUnique(String roleKey);

    SysRole selectRoleById(Long roleId);

    int insertRole(SysRole role);
}
