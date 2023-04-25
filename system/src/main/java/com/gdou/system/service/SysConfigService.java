package com.gdou.system.service;

import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysConfig;

import java.util.Map;

public interface SysConfigService {
    boolean selectCaptchaEnabled();

    String selectConfigByKey(String configKey);

    void loadingConfigCache();

    SysConfig selectConfigById(Long configId);

   PageVo<SysConfig> selectConfigList(Map<String, String> queryCondition);

    void resetConfigCache();

    void clearConfigCache();

    boolean checkConfigKeyUnique(SysConfig config);

    int insertConfig(SysConfig config);

    int updateConfig(SysConfig config);

    void deleteConfigByIds(Long[] configIds);
}
