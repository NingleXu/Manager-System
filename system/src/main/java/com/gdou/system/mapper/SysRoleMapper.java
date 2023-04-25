package com.gdou.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdou.common.domain.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRolePermissionByUserId(Long userId);

    List<SysRole> selectRolesByUsername(String username);

    List<SysRole> selectRoleList(SysRole role);

    int updateRole(SysRole role);

    int deleteRoleByIds(Long[] roleIds);

    SysRole checkRoleNameUnique(String roleName);

    SysRole checkRoleKeyUnique(String roleKey);

    SysRole selectRoleById(Long roleId);
}
