package com.gdou.system.service;

import com.gdou.common.domain.entity.SysLogininfor;

import java.util.List;

public interface SysLogininforService {

    public void insertLogininfor(SysLogininfor sysLogininfor);


    List<SysLogininfor> selectLogininforList(SysLogininfor queryCondition);

    int deleteLogininforByIds(Long[] infoIds);

    void cleanLogininfor();

}
