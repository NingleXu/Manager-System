package com.gdou.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysLogininfor;
import com.gdou.common.domain.entity.SysOperLog;
import com.gdou.common.utils.StringUtils;
import com.gdou.system.mapper.SysOperLogMapper;
import com.gdou.system.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

import static com.gdou.common.constant.PageConstants.*;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {
    @Autowired
    private SysOperLogMapper sysOperLogMapper;

    @Override
    public void insertOperlog(SysOperLog sysOperLog) {
        sysOperLog.setOperTime(new Date());
        sysOperLogMapper.insert(sysOperLog);
    }

    @Override
    public PageVo<SysOperLog> selectOperLogList(Map<String, String> queryCondition) {
        String is_Asc = queryCondition.get(IS_ASC);
        String orderColumn = queryCondition.get(ORDER_BY_COLUMN);
        LambdaQueryWrapper<SysOperLog> queryWrapper = new LambdaQueryWrapper<SysOperLog>()
                .like(StringUtils.isNotNull(queryCondition.get(OPER_NAME)), SysOperLog::getOperName,
                        queryCondition.get(OPER_NAME))
                .like(StringUtils.isNotNull(queryCondition.get(TITLE)), SysOperLog::getTitle,
                        queryCondition.get(TITLE))
                .eq(StringUtils.isNotNull(queryCondition.get(BUSINESS_TYPE)), SysOperLog::getBusinessType,
                        queryCondition.get(BUSINESS_TYPE))
                .eq(StringUtils.isNotNull(queryCondition.get(STATUS)), SysOperLog::getStatus,
                        queryCondition.get(STATUS))
                .between(StringUtils.isNotNull(queryCondition.get(OPER_TIME)),
                        SysOperLog::getOperTime,
                        queryCondition.get(BEGIN_TIME), queryCondition.get(END_TIME));

        if (StringUtils.isNull(is_Asc)) {
            queryWrapper.orderByDesc(SysOperLog::getOperId);
        } else if (OPER_TIME.equals(orderColumn)) {
            queryWrapper.orderBy(true, ASC.equals(is_Asc), SysOperLog::getOperTime);
        } else if (OPER_NAME.equals(orderColumn)) {
            queryWrapper.orderBy(true, ASC.equals(is_Asc), SysOperLog::getOperName);
        } else {
            queryWrapper.orderBy(true, ASC.equals(is_Asc), SysOperLog::getCostTime);
        }

        Page<SysOperLog> page = new Page<>(Long.parseLong(queryCondition.get(PAGE_NUM)),
                Long.parseLong(queryCondition.get(PAGE_SIZE)));
        baseMapper.selectPage(page, queryWrapper);
        return new PageVo<>(page.getRecords(), page.getTotal());
    }

    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        return baseMapper.delete(new LambdaQueryWrapper<SysOperLog>()
                .in( SysOperLog::getOperId, operIds));
    }

    @Override
    public void cleanOperLog() {
        baseMapper.cleanOperLog();
    }
}
