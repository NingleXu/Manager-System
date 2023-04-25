package com.gdou.system.service;


import com.gdou.common.domain.entity.SysRole;
import com.gdou.common.domain.entity.SysUserRole;

import java.util.List;
import java.util.Set;

public interface SysRoleService {
    Set<String> selectRolePermissionByUserId(Long userId);

    List<SysRole> selectRoleList(SysRole role);

    void checkRoleDataScope(Long roleId);

    SysRole selectRoleById(Long roleId);

    boolean checkRoleNameUnique(SysRole role);

    boolean checkRoleKeyUnique(SysRole role);

    int insertRole(SysRole role);

    void checkRoleAllowed(SysRole role);

    int updateRole(SysRole role);

    int deleteRoleByIds(Long[] roleIds);

    int countUserRoleByRoleId(Long roleId);

    List<SysRole> selectRoleAll();

    List<SysRole> selectRoleAll(SysRole role);

    List<SysRole> selectRolesByUserId(Long userId);

    int updateRoleStatus(SysRole role);


    int deleteAuthUser(SysUserRole userRole);

    int deleteAuthUsers(Long roleId, Long[] userIds);

    int insertAuthUsers(Long roleId, Long[] userIds);
}
