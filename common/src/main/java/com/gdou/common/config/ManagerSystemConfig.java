package com.gdou.common.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 管理系统全局配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "manager")
public class ManagerSystemConfig {

    /**
     * 验证码类型
     */
    private static String captchaType;


    /** 获取地址开关 */
    private static boolean addressEnabled;

    public static boolean isAddressEnabled()
    {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled)
    {
        ManagerSystemConfig.addressEnabled = addressEnabled;
    }

    public static String getCaptchaType() {
        return captchaType;
    }

    public void setCaptchaType(String captchaType) {
        ManagerSystemConfig.captchaType = captchaType;
    }
}
