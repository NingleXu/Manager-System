package com.gdou.admin.controller;


import com.gdou.common.annotaion.Log;
import com.gdou.common.domain.PageVo;
import com.gdou.common.domain.R;
import com.gdou.common.domain.entity.SysConfig;
import com.gdou.common.enums.BusinessType;
import com.gdou.system.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.gdou.common.utils.SecurityUtils.getUsername;

@RestController
@RequestMapping("/system/config")
public class SysConfigController {

    @Autowired
    private SysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@check.hasPermi('system:config:list')")
    @GetMapping("/list")
    public R list(@RequestParam Map<String, String> queryCondition) {
        return R.success(configService.selectConfigList(queryCondition));
    }

    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@check.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public R getInfo(@PathVariable Long configId) {
        return R.success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public R getConfigKey(@PathVariable String configKey) {
        return R.success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @PreAuthorize("@check.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody SysConfig config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return R.error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(getUsername());
        return R.success(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @PreAuthorize("@check.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody SysConfig config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return R.error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(getUsername());
        return R.success(configService.updateConfig(config));
    }


    /**
     * 删除参数配置
     */
    @PreAuthorize("@check.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public R remove(@PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);
        return R.success("成功！");
    }

    /**
     * 刷新参数缓存
     */
    @PreAuthorize("@check.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R refreshCache() {
        configService.resetConfigCache();
        return R.success("成功");
    }

}
