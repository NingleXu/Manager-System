package com.gdou.admin.controller;


import com.gdou.common.config.ManagerSystemConfig;
import com.gdou.common.constant.CacheConstants;
import com.gdou.common.constant.Constants;
import com.gdou.common.domain.R;
import com.gdou.common.utils.Base64;
import com.gdou.common.utils.IdUtils;
import com.gdou.common.utils.MapUtil;
import com.gdou.common.utils.RedisCache;
import com.gdou.system.service.SysConfigService;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
public class CaptchaController {

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysConfigService configService;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public R getCode() {
        //查询是否开起来缓存
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (!captchaEnabled) {
            return R.success(MapUtil.builder()
                    .put("captchaEnabled", captchaEnabled)
                    .build());
        }
        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;

        // 生成验证码
        String captchaType = ManagerSystemConfig.getCaptchaType();
        if ("math".equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return R.error(e.getMessage());
        }


        return R.success(MapUtil.builder()
                .put("uuid", uuid)
                .put("img", Base64.encode(os.toByteArray()))
                .put("captchaEnabled", captchaEnabled)
                .build());
    }
}
