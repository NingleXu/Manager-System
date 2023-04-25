package com.gdou.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdou.common.domain.entity.SysRole;
import com.gdou.common.domain.entity.SysUser;
import com.gdou.common.domain.entity.SysUserRole;
import com.gdou.common.exception.ServiceException;
import com.gdou.common.utils.DateUtils;
import com.gdou.common.utils.StringUtils;
import com.gdou.common.utils.ip.IpUtils;
import com.gdou.system.mapper.SysRoleMapper;
import com.gdou.system.mapper.SysUserMapper;
import com.gdou.system.mapper.SysUserRoleMapper;
import com.gdou.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.gdou.common.constant.UserConstants.*;


@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserMapper userMapper;

    /**
     * @author xzh
     * @time 2023/4/11 15:45
     * 查询用户列表
     */
    @Override
    public List<SysUser> selectUserList(SysUser sysUser) {
        return userMapper.selectUserList(sysUser);
    }

    /**
     * @author xzh
     * @time 2023/4/8 17:15
     * 根据用户名查询用户信息
     */
    @Override
    public SysUser selectUserByUserName(String username) {
        return userMapper.selectUserByUserName(username);
    }

    /**
     * @author xzh
     * @time 2023/4/8 17:15
     * 根据用户名查询用户角色
     */
    @Override
    public String selectUserRoleGroup(String username) {
        List<SysRole> roles = roleMapper.selectRolesByUsername(username);
        if (roles.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return roles.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * @author xzh
     * @time 2023/4/14 20:40
     * 通过用户id查询用户信息
     */
    @Override
    public SysUser selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * @author xzh
     * @time 2023/4/14 20:58
     * 修改用户状态
     */
    @Override
    public int updateUserStatus(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * @author xzh
     * @time 2023/4/14 21:06
     * 检查用户账户是否唯一
     */
    @Override
    public boolean checkUserNameUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkUserNameUnique(user.getUserName());

        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId) {
            return NOT_UNIQUE;
        }
        return UNIQUE;
    }

    /**
     * @author xzh
     * @time 2023/4/14 21:06
     * 检查用户手机号码是否唯一
     */
    @Override
    public boolean checkPhoneUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());

        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId) {
            return NOT_UNIQUE;
        }
        return UNIQUE;
    }

    /**
     * @author xzh
     * @time 2023/4/14 21:12
     * 检查用户邮箱是否唯一
     */
    @Override
    public boolean checkEmailUnique(SysUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());

        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId) {
            return NOT_UNIQUE;
        }
        return UNIQUE;
    }

    /**
     * @author xzh
     * @time 2023/4/14 21:15
     * 修改用户信息
     */
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(userId, user.getRoleIds());
        return userMapper.updateUser(user);
    }

    /**
     * 新增用户角色信息
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds) {
        if (StringUtils.isNotEmpty(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<>(roleIds.length);
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * @author xzh
     * @time 2023/4/14 21:26
     * 新增用户
     */
    @Override
    @Transactional
    public int insertUser(SysUser user) {
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户与角色管理
        insertUserRole(user.getUserId(), user.getRoleIds());
        return rows;
    }

    /**
     * @author xzh
     * @time 2023/4/14 21:36
     * 删除用户
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            checkUserAllowed(new SysUser(userId));
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);

        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUser.setLoginDate(DateUtils.getNowDate());
        updateUserProfile(sysUser);
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user) {
        return userMapper.updateUser(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * @author xzh
     * @time 2023/4/15 19:46
     * 修改用户密码
     */
    @Override
    public boolean resetPwd(SysUser user) {
        return this.update(new SysUser(), new LambdaUpdateWrapper<SysUser>()
                .set(SysUser::getPassword, user.getPassword())
                .eq(SysUser::getUserId, user.getUserId()));
    }

    /**
     * @author xzh
     * @time 2023/4/15 20:01
     */
    @Override
    public List<SysUser> selectAllocatedList(SysUser user) {
        return userMapper.selectAllocatedList(user);
    }

    @Override
    public  List<SysUser> selectUnallocatedList(SysUser user) {
        return userMapper.selectUnallocatedList(user);

    }
}
