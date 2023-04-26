package com.gdou.system.mapper;

import com.gdou.common.domain.entity.SysOperLog;

import java.util.List;


public interface SysOperLogMapper{
    void cleanOperLog();

    List<SysOperLog> selectOperLogList(SysOperLog operLog);

    int insertSysOperLog(SysOperLog sysOperLog);

    int deleteOperLogByIds(Long[] operIds);

}
