package com.gdou.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdou.common.domain.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;


public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
    void cleanOperLog();

}
