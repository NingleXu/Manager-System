package com.gdou.common.constant;

/**
 * 缓存的key 常量
 */
public class CacheConstants {

    /**
     * 验证码 key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";


    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";


    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

}
