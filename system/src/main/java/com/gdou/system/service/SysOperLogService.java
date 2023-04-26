package com.gdou.system.service;

import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysOperLog;

import java.util.List;
import java.util.Map;

public interface SysOperLogService {
    public void insertOperlog(SysOperLog sysOperLog);

    List<SysOperLog> selectOperLogList(SysOperLog operLog);

    int deleteOperLogByIds(Long[] operIds);

    void cleanOperLog();

}
