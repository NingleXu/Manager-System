package com.gdou.system.service;

import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysOperLog;

import java.util.Map;

public interface SysOperLogService {
    public void insertOperlog(SysOperLog sysOperLog);

    PageVo<SysOperLog> selectOperLogList(Map<String, String> sysOperLog);

    int deleteOperLogByIds(Long[] operIds);

    void cleanOperLog();

}
