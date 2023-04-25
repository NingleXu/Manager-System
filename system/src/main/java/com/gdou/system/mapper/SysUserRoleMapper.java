package com.gdou.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdou.common.domain.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    int batchUserRole(List<SysUserRole> list);

    int deleteUserRoleByUserId(Long userId);

    int deleteUserRole(Long[] userIds);

    int countUserRoleByRoleId(Long roleId);

    int deleteUserRoleInfos(Long roleId, Long[] userIds);

    int deleteUserRoleInfo(SysUserRole userRole);

}
