package com.gdou.system.service;

import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysUser;

import java.util.Map;

public interface SysUserService {
    SysUser selectUserByUserName(String username);

    String selectUserRoleGroup(String username);

    PageVo<SysUser> selectUserList(Map<String, String> queryMap);

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

    void insertUserAuth(Long userId, Long[] roleIds);

    boolean resetPwd(SysUser user);

    PageVo<SysUser> selectAllocatedList(Map<String, String> user);

    PageVo<SysUser> selectUnallocatedList(Map<String, String> queryMap);
}
