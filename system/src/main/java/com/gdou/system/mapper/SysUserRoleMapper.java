package com.gdou.system.mapper;


import com.gdou.common.domain.entity.SysUserRole;


import java.util.List;


public interface SysUserRoleMapper {
    int batchUserRole(List<SysUserRole> list);

    int deleteUserRoleByUserId(Long userId);

    int deleteUserRole(Long[] userIds);

    int countUserRoleByRoleId(Long roleId);

    int deleteUserRoleInfos(Long roleId, Long[] userIds);

    int deleteUserRoleInfo(SysUserRole userRole);

}
