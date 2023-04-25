package com.gdou.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysConfig;
import com.gdou.common.domain.entity.SysLogininfor;
import com.gdou.common.utils.StringUtils;
import com.gdou.system.mapper.SysLogininforMapper;
import com.gdou.system.service.SysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.LockInfo;
import java.util.Map;

import static com.gdou.common.constant.PageConstants.*;

@Service
public class SysLogininforServiceImpl extends ServiceImpl<SysLogininforMapper, SysLogininfor> implements SysLogininforService {

    @Autowired
    private SysLogininforMapper logininforMapper;

    @Override
    public void insertLogininfor(SysLogininfor sysLogininfor) {
        logininforMapper.insert(sysLogininfor);
    }

    @Override
    public PageVo<SysLogininfor> selectLogininforList(Map<String, String> queryCondition) {
        String userName = queryCondition.get(USER_NAME);
        String ipaddr = queryCondition.get(IP_ADDR);
        String status = queryCondition.get(STATUS);
        String startTime = queryCondition.get(BEGIN_TIME);
        String endTime = queryCondition.get(END_TIME);
        String is_Asc = queryCondition.get(IS_ASC);
        String orderColumn = queryCondition.get(ORDER_BY_COLUMN);

        LambdaQueryWrapper<SysLogininfor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotNull(userName), SysLogininfor::getUserName, userName)
                .like(StringUtils.isNotNull(ipaddr), SysLogininfor::getIpaddr, ipaddr)
                .eq(StringUtils.isNotNull(status), SysLogininfor::getStatus, status)
                .between(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime),
                        SysLogininfor::getLoginTime, startTime, endTime);
        if (StringUtils.isNull(is_Asc)) {
            queryWrapper.orderByDesc(SysLogininfor::getInfoId);
        } else if (LOGIN_TIME.equals(orderColumn)) {
            queryWrapper.orderBy(true, ASC.equals(is_Asc), SysLogininfor::getLoginTime);
        } else {
            queryWrapper.orderBy(true, ASC.equals(is_Asc), SysLogininfor::getUserName);
        }


        Page<SysLogininfor> page = new Page<>(Long.parseLong(queryCondition.get(PAGE_NUM)),
                Long.parseLong(queryCondition.get(PAGE_SIZE)));
        baseMapper.selectPage(page, queryWrapper);
        return new PageVo<>(page.getRecords(), page.getTotal());
    }

    @Override
    public int deleteLogininforByIds(Long[] infoIds) {
        return baseMapper.delete(new LambdaQueryWrapper<SysLogininfor>()
                .in(SysLogininfor::getInfoId, infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {
        logininforMapper.cleanLogininfor();
    }
}
