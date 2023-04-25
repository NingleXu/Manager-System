package com.gdou.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysRole;
import com.gdou.common.domain.entity.SysUser;
import com.gdou.common.domain.entity.SysUserRole;
import com.gdou.common.exception.ServiceException;
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

import static com.gdou.common.constant.Constants.*;
import static com.gdou.common.constant.PageConstants.*;
import static com.gdou.common.constant.UserConstants.NO_EXIST;


@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    /**
     * @author xzh
     * @time 2023/4/11 15:45
     * 查询用户列表
     */
    @Override
    public PageVo<SysUser> selectUserList(Map<String, String> queryCondition) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        String startTime = queryCondition.get(BEGIN_TIME);
        String endTime = queryCondition.get(END_TIME);

        queryWrapper.like(StringUtils.isNotEmpty(queryCondition.get(USER_NAME)),
                        SysUser::getUserName, queryCondition.get(USER_NAME))
                .like(StringUtils.isNotEmpty(queryCondition.get(USER_PHONE_NUMBER)),
                        SysUser::getPhonenumber, queryCondition.get(USER_PHONE_NUMBER))
                .eq(StringUtils.isNotEmpty(queryCondition.get(STATUS)),
                        SysUser::getStatus, queryCondition.get(STATUS))
                .between(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime),
                        SysUser::getCreateTime, startTime, endTime)
                .select(SysUser.class, i -> !i.getColumn().equals("password"))
                .eq(SysUser::getDelFlag, NO_DELETE);


        Page<SysUser> page = new Page<>(Long.parseLong(queryCondition.get(PAGE_NUM)),
                Long.parseLong(queryCondition.get(PAGE_SIZE)));
        baseMapper.selectPage(page, queryWrapper);
        return new PageVo<>(page.getRecords(), page.getTotal());
    }

    /**
     * @author xzh
     * @time 2023/4/8 17:15
     * 根据用户名查询用户信息
     */
    @Override
    public SysUser selectUserByUserName(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserName, username)
                .eq(SysUser::getDelFlag, NO_DELETE);
        return this.getOne(queryWrapper);
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
        return baseMapper.selectById(userId);
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
        return baseMapper.updateById(user);
    }

    /**
     * @author xzh
     * @time 2023/4/14 21:06
     * 检查用户账户是否唯一
     */
    @Override
    public boolean checkUserNameUnique(SysUser user) {

        return baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, user.getUserName())
                .ne(StringUtils.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId())
                .eq(SysUser::getDelFlag, NO_DELETE)) == NO_EXIST;
    }

    /**
     * @author xzh
     * @time 2023/4/14 21:06
     * 检查用户手机号码是否唯一
     */
    @Override
    public boolean checkPhoneUnique(SysUser user) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhonenumber, user.getPhonenumber())
                .ne(StringUtils.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId())
                .eq(SysUser::getDelFlag, NO_DELETE)) == NO_EXIST;
    }

    /**
     * @author xzh
     * @time 2023/4/14 21:12
     * 检查用户邮箱是否唯一
     */
    @Override
    public boolean checkEmailUnique(SysUser user) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, user.getEmail())
                .ne(StringUtils.isNotNull(user.getUserId()), SysUser::getUserId, user.getUserId())
                .eq(SysUser::getDelFlag, NO_DELETE)) == NO_EXIST;
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
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));
        // 新增用户与角色管理
        insertUserRole(userId, user.getRoleIds());
        return baseMapper.updateUser(user);
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
        int rows = baseMapper.insert(user);
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
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getUserId, userIds));

        return baseMapper.update(new SysUser(), new LambdaUpdateWrapper<SysUser>()
                .in(SysUser::getUserId, userIds)
                .set(SysUser::getDelFlag, DELETE));
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {
        this.update(new SysUser(), new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getUserId, userId)
                .set(SysUser::getLoginIp, IpUtils.getIpAddr()));
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
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));
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
    public PageVo<SysUser> selectAllocatedList(Map<String, String> queryCondition) {
        //查询当前角色下的用户id
        List<Long> userIds = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getRoleId, queryCondition.get(ROLE_ID)))
                .stream().map(SysUserRole::getUserId).distinct().toList();

        if (StringUtils.isEmpty(userIds)) {
            return new PageVo<>(Collections.emptyList(), ZERO);
        }

        String userName = queryCondition.get(USER_NAME);
        String userPhoneNumber = queryCondition.get(USER_PHONE_NUMBER);
        LambdaQueryWrapper<SysUser> userQuery = new LambdaQueryWrapper<SysUser>()
                .in(SysUser::getUserId, userIds)
                .like(StringUtils.isNotEmpty(userName), SysUser::getUserName, userName)
                .like(StringUtils.isNotEmpty(userPhoneNumber), SysUser::getPhonenumber, userPhoneNumber)
                .eq(SysUser::getDelFlag,NO_DELETE)
                .select(SysUser.class, i -> !i.getColumn().equals("password"));

        Page<SysUser> page = new Page<>(Long.parseLong(queryCondition.get(PAGE_NUM)),
                Long.parseLong(queryCondition.get(PAGE_SIZE)));

        baseMapper.selectPage(page, userQuery);

        return new PageVo<>(page.getRecords(), page.getTotal());
    }

    @Override
    public PageVo<SysUser> selectUnallocatedList(Map<String, String> queryCondition) {
        //查询当前角色下的用户id
        List<Long> userIds = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getRoleId, queryCondition.get(ROLE_ID)))
                .stream().map(SysUserRole::getUserId).distinct().toList();

        String userName = queryCondition.get(USER_NAME);
        String userPhoneNumber = queryCondition.get(USER_PHONE_NUMBER);
        LambdaQueryWrapper<SysUser> userQuery = new LambdaQueryWrapper<SysUser>()
                .notIn(StringUtils.isNotEmpty(userIds),SysUser::getUserId, userIds)
                .like(StringUtils.isNotEmpty(userName), SysUser::getUserName, userName)
                .like(StringUtils.isNotEmpty(userPhoneNumber), SysUser::getPhonenumber, userPhoneNumber)
                .eq(SysUser::getDelFlag,NO_DELETE)
                .select(SysUser.class, i -> !i.getColumn().equals("password"));

        Page<SysUser> page = new Page<>(Long.parseLong(queryCondition.get(PAGE_NUM)),
                Long.parseLong(queryCondition.get(PAGE_SIZE)));

        baseMapper.selectPage(page, userQuery);

        return new PageVo<>(page.getRecords(), page.getTotal());

    }
}
