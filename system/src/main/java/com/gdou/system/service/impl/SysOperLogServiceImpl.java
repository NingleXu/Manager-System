package com.gdou.system.service.impl;


import com.gdou.common.domain.entity.SysOperLog;
import com.gdou.system.mapper.SysOperLogMapper;
import com.gdou.system.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SysOperLogServiceImpl  implements SysOperLogService {
    @Autowired
    private SysOperLogMapper operLogMapper;

    @Override
    public void insertOperlog(SysOperLog sysOperLog) {
        sysOperLog.setOperTime(new Date());
        operLogMapper.insertSysOperLog(sysOperLog);
    }

    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {

        return operLogMapper.selectOperLogList(operLog);
    }

    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        return operLogMapper.deleteOperLogByIds(operIds);
    }

    @Override
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }
}
