package com.gdou.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdou.common.domain.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
    int batchRoleMenu(List<SysRoleMenu> list);

    int deleteRoleMenuByRoleId(Long roleId);

    int deleteRoleMenu(Long[] roleIds);
}
