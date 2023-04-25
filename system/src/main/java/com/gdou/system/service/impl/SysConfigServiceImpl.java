package com.gdou.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdou.common.constant.CacheConstants;
import com.gdou.common.constant.UserConstants;
import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.entity.SysConfig;
import com.gdou.common.exception.ServiceException;
import com.gdou.common.utils.RedisCache;
import com.gdou.common.utils.StringUtils;
import com.gdou.common.utils.text.Convert;
import com.gdou.system.mapper.SysConfigMapper;
import com.gdou.system.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.gdou.common.constant.PageConstants.*;
import static com.gdou.common.constant.UserConstants.NO_EXIST;


@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {


    @Autowired
    private RedisCache redisCache;


    //默认加载到缓存中
    @PostConstruct
    public void init() {
        loadingConfigCache();
    }


    @Override
    public PageVo<SysConfig> selectConfigList(Map<String, String> queryCondition) {
        String configName = queryCondition.get(CONFIG_NAME);
        String configKey = queryCondition.get(CONFIG_KEY);
        String configType = queryCondition.get(CONFIG_TYPE);
        String startTime = queryCondition.get(BEGIN_TIME);
        String endTime = queryCondition.get(END_TIME);

        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotNull(configName), SysConfig::getConfigName, configName)
                .like(StringUtils.isNotNull(configKey), SysConfig::getConfigKey, configKey)
                .eq(StringUtils.isNotNull(configType), SysConfig::getConfigType, configType)
                .between(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime),
                        SysConfig::getCreateTime, startTime, endTime);

        Page<SysConfig> page = new Page<>(Long.parseLong(queryCondition.get(PAGE_NUM)),
                Long.parseLong(queryCondition.get(PAGE_SIZE)));
        baseMapper.selectPage(page, queryWrapper);
        return new PageVo<>(page.getRecords(), page.getTotal());
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled() {
        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled)) {
            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfig selectConfigById(Long configId) {
        return baseMapper.selectById(configId);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        //查询缓存
        String configValue = Convert.toStr(redisCache.getCacheObject(CacheConstants.SYS_CONFIG_KEY + configKey));
        if (StringUtils.isNotEmpty(configValue)) {
            return configValue;
        }
        //查询数据库
        SysConfig retConfig = baseMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey));
        if (StringUtils.isNotNull(retConfig)) {
            redisCache.setCacheObject(CacheConstants.SYS_CONFIG_KEY + configKey, retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configsList = baseMapper.selectList(null);
        for (SysConfig config : configsList) {
            redisCache.setCacheObject(CacheConstants.SYS_CONFIG_KEY + config.getConfigKey(), config.getConfigValue());
        }
    }

    /**
     * 判断配置key是否唯一
     *
     * @param config
     * @return
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        return baseMapper.selectCount(new LambdaQueryWrapper<SysConfig>()
                .ne(StringUtils.isNotNull(config.getConfigId()), SysConfig::getConfigId, config.getConfigId())
                .eq(SysConfig::getConfigKey, config.getConfigKey())) == NO_EXIST;
    }

    @Override
    public int insertConfig(SysConfig config) {
        int row = baseMapper.insert(config);
        if (row > 0) {
            redisCache.setCacheObject(CacheConstants.SYS_CONFIG_KEY + config.getConfigValue(),
                    config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config) {
        SysConfig temp = baseMapper.selectById(config.getConfigId());
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey())) {
            redisCache.deleteObject(CacheConstants.SYS_CONFIG_KEY + temp.getConfigKey());
        }
        int row = baseMapper.updateById(config);
        if (row > 0) {
            redisCache.setCacheObject(CacheConstants.SYS_CONFIG_KEY + config.getConfigKey(), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            baseMapper.delete(new LambdaQueryWrapper<SysConfig>()
                    .eq(SysConfig::getConfigId, configId));
            redisCache.deleteObject(CacheConstants.SYS_CONFIG_KEY + config.getConfigKey());
        }
    }


    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }
}
