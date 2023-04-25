package com.gdou.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysRole;
import com.gdou.common.domain.entity.SysRoleMenu;
import com.gdou.common.domain.entity.SysUser;
import com.gdou.common.domain.entity.SysUserRole;
import com.gdou.common.exception.ServiceException;
import com.gdou.common.utils.StringUtils;
import com.gdou.system.mapper.SysRoleMapper;
import com.gdou.system.mapper.SysRoleMenuMapper;
import com.gdou.system.mapper.SysUserMapper;
import com.gdou.system.mapper.SysUserRoleMapper;
import com.gdou.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

import static com.gdou.common.constant.Constants.DELETE;
import static com.gdou.common.constant.Constants.NO_DELETE;
import static com.gdou.common.constant.PageConstants.*;
import static com.gdou.common.constant.UserConstants.NO_EXIST;
import static com.gdou.common.utils.SecurityUtils.getUserId;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        return userRoles.stream().distinct()
                .map(SysRole::getRoleKey).collect(Collectors.toSet());
    }

    /**
     * @author xzh
     * @time 2023/4/12 9:36
     * 查询所有角色
     */
    @Override
    public PageVo<SysRole> selectRoleList(Map<String, String> queryCondition) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        String beginTime = queryCondition.get(BEGIN_TIME);
        String endTime = queryCondition.get(END_TIME);
        queryWrapper.like(StringUtils.isNotEmpty(queryCondition.get(ROLE_NAME)),
                        SysRole::getRoleName, queryCondition.get(ROLE_NAME))
                .like(StringUtils.isNotEmpty(queryCondition.get(ROLE_KEY)),
                        SysRole::getRoleId, queryCondition.get(ROLE_KEY))
                .eq(StringUtils.isNotEmpty(queryCondition.get(STATUS)),
                        SysRole::getStatus, queryCondition.get(STATUS))
                .between(StringUtils.isNotEmpty(beginTime) && StringUtils.isNotEmpty(endTime),
                        SysRole::getCreateTime, beginTime, endTime)
                .eq(SysRole::getDelFlag, NO_DELETE);

        Page<SysRole> page = new Page<>(Long.parseLong(queryCondition.get(PAGE_NUM)),
                Long.parseLong(queryCondition.get(PAGE_SIZE)));
        baseMapper.selectPage(page, queryWrapper);
        return new PageVo<>(page.getRecords(), page.getTotal());
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    @Override
    public void checkRoleDataScope(Long roleId) {
        if (!SysUser.isAdmin(getUserId())) {
            throw new ServiceException("没有权限访问角色数据！");
        }
    }

    /**
     * @author xzh
     * @time 2023/4/12 10:08
     * 查询一个角色详细信息
     */
    @Override
    public SysRole selectRoleById(Long roleId) {
        return baseMapper.selectById(roleId);
    }


    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        //id不相同 名字相同
        Integer count = baseMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, role.getRoleName()) //名字相等
                .ne(StringUtils.isNotNull(role.getRoleId()), SysRole::getRoleId, role.getRoleId())
                .eq(SysRole::getDelFlag, NO_DELETE));
        return count == NO_EXIST;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        //id不相同 key系统
        Integer count = baseMapper.selectCount(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleKey, role.getRoleKey()) //名字相等
                .ne(StringUtils.isNotNull(role.getRoleId()), SysRole::getRoleId, role.getRoleId())
                .eq(SysRole::getDelFlag, NO_DELETE));
        return count == NO_EXIST;
    }

    @Override
    public int insertRole(SysRole role) {
        //插入角色
        roleMapper.insert(role);
        //插入角色菜单表
        return insertRoleMenu(role);
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    public int insertRoleMenu(SysRole role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }

    /**
     * 校验是否运行修改
     *
     * @param role
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 修改角色信息
     *
     * @param role
     * @return
     */
    @Override
    public int updateRole(SysRole role) {
        // 修改角色信息
        roleMapper.updateById(role);
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        return insertRoleMenu(role);
    }

    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new SysRole(roleId));
            checkRoleDataScope(roleId);
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .in(SysRoleMenu::getRoleId, roleIds));

        return roleMapper.update(new SysRole(), new LambdaUpdateWrapper<SysRole>()
                .in(SysRole::getRoleId, roleIds)
                .set(SysRole::getDelFlag, DELETE));
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, roleId));
    }

    /**
     * @author xzh
     * @time 2023/4/14 20:23
     * 查询所有角色
     */
    @Override
    public List<SysRole> selectRoleAll() {
        return baseMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getDelFlag, NO_DELETE));
    }

    /**
     * @author xzh
     * @time 2023/4/14 20:35
     * 查询用户的的角色
     */
    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        List<SysRole> roles = selectRoleAll();
        for (SysRole role : roles) {
            for (SysRole userRole : userRoles) {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 修改角色状态
     *
     * @param role
     * @return
     */
    @Override
    public int updateRoleStatus(SysRole role) {
        boolean result = this.update(new SysRole(), new LambdaUpdateWrapper<SysRole>()
                .eq(SysRole::getRoleId, role.getRoleId())
                .set(SysRole::getStatus, role.getStatus())
                .set(SysRole::getUpdateBy, role.getUpdateBy())
                .set(SysRole::getUpdateTime, new Date()));
        return result ? 1 : 0;
    }

    /**
     * 通过用户id查询拥有的角色id
     *
     * @param userId
     * @return
     */
    @Override
    public long[] selectUserRoleIds(Long userId) {

        return userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId))
                .stream().mapToLong(SysUserRole::getRoleId).toArray();
    }

    /**
     * @author xzh
     * @time 2023/4/16 16:44
     * 取消用户授权
     */
    @Override
    public int deleteAuthUser(SysUserRole userRole) {
        return userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, userRole.getRoleId())
                .eq(SysUserRole::getUserId, userRole.getUserId()));
    }

    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        return userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, roleId)
                .in(SysUserRole::getUserId, userIds));
    }

    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds) {
        // 新增用户与角色管理
        List<SysUserRole> list = new ArrayList<SysUserRole>();
        for (Long userId : userIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        return userRoleMapper.batchUserRole(list);
    }
}
