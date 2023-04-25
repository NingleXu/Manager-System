package com.gdou.system.mapper;


import com.gdou.common.domain.entity.SysConfig;

import java.util.List;


public interface SysConfigMapper {
    List<SysConfig> selectConfigList(SysConfig sysConfig);

    SysConfig selectConfigById(Long configId);

    SysConfig selectConfig(SysConfig sysConfig);

    SysConfig checkConfigKeyUnique(String configKey);

    int insertConfig(SysConfig config);

    int updateConfig(SysConfig config);

    void deleteConfigById(Long configId);
}
