package com.gdou.system.service.impl;


import com.gdou.common.domain.entity.SysLogininfor;
import com.gdou.system.mapper.SysLogininforMapper;
import com.gdou.system.service.SysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SysLogininforServiceImpl  implements SysLogininforService {

    @Autowired
    private SysLogininforMapper logininforMapper;

    @Override
    public void insertLogininfor(SysLogininfor sysLogininfor) {
        logininforMapper.insertLogininfor(sysLogininfor);
    }

    @Override
    public List<SysLogininfor>  selectLogininforList(SysLogininfor logininfor) {
       return logininforMapper.selectLogininforList(logininfor);
    }

    @Override
    public int deleteLogininforByIds(Long[] infoIds) {
        return logininforMapper.deleteLogininforByIds(infoIds);
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {
        logininforMapper.cleanLogininfor();
    }
}
