package com.gdou.system.service;

import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysLogininfor;

import java.util.Map;

public interface SysLogininforService {

    public void insertLogininfor(SysLogininfor sysLogininfor);


    PageVo<SysLogininfor> selectLogininforList(Map<String, String> queryCondition);

    int deleteLogininforByIds(Long[] infoIds);

    void cleanLogininfor();

}
