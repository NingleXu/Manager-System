package com.gdou.system.service;


import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysRole;
import com.gdou.common.domain.entity.SysUserRole;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SysRoleService {
    Set<String> selectRolePermissionByUserId(Long userId);

    PageVo<SysRole> selectRoleList(Map<String, String> queryMap);

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

    List<SysRole> selectRolesByUserId(Long userId);

    int updateRoleStatus(SysRole role);

    long[] selectUserRoleIds(Long userId);

    int deleteAuthUser(SysUserRole userRole);

    int deleteAuthUsers(Long roleId, Long[] userIds);

    int insertAuthUsers(Long roleId, Long[] userIds);
}
