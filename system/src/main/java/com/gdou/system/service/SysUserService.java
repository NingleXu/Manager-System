package com.gdou.system.service;

import com.gdou.common.domain.entity.SysUser;

import java.util.List;

public interface SysUserService {
    SysUser selectUserByUserName(String username);

    String selectUserRoleGroup(String username);

    List<SysUser> selectUserList(SysUser queryMap);

    SysUser selectUserById(Long userId);

    void checkUserAllowed(SysUser user);

    int updateUserStatus(SysUser user);

    boolean checkUserNameUnique(SysUser user);

    boolean checkPhoneUnique(SysUser user);

    boolean checkEmailUnique(SysUser user);

    int updateUser(SysUser user);

    int insertUser(SysUser user);

    int deleteUserByIds(Long[] userIds);

    void recordLoginInfo(Long userId);

    int updateUserProfile(SysUser user);

    void insertUserAuth(Long userId, Long[] roleIds);

    int resetPwd(SysUser user);

    List<SysUser> selectAllocatedList(SysUser user);

    List<SysUser> selectUnallocatedList(SysUser user);

    int resetUserPwd(String userName, String password);
}
