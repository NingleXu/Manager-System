package com.gdou.system.mapper;

import com.gdou.common.domain.entity.SysLogininfor;

import java.util.List;


public interface SysLogininforMapper {
    void cleanLogininfor();
    List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);

    int deleteLogininforByIds(Long[] infoIds);

    int insertLogininfor(SysLogininfor sysLogininfor);
}
