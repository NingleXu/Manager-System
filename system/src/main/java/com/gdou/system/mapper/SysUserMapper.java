package com.gdou.system.mapper;


import com.gdou.common.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysUserMapper {
    int updateUser(SysUser user);

    /**
     * 根据分页条件查询用户
     * @param sysUser
     * @return
     */
    List<SysUser> selectUserList(SysUser sysUser);

    SysUser selectUserByUserName(String username);

    SysUser checkPhoneUnique(String phonenumber);

    SysUser checkEmailUnique(String email);

    SysUser checkUserNameUnique(String userName);

    int insertUser(SysUser user);

    SysUser selectUserById(Long userId);

    int deleteUserByIds(Long[] userIds);

    /**
     * 根据条件分页查询已配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUnallocatedList(SysUser user);

    int resetUserPwd(String userName, String password);
}
