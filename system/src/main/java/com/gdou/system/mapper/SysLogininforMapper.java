package com.gdou.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gdou.common.domain.entity.SysLogininfor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysLogininforMapper extends BaseMapper<SysLogininfor> {
    void cleanLogininfor();
}
