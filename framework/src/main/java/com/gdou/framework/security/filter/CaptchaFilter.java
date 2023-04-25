package com.gdou.framework.security.filter;


import com.alibaba.fastjson2.JSON;
import com.gdou.common.constant.CacheConstants;
import com.gdou.common.constant.Constants;
import com.gdou.common.constant.UrlConstants;
import com.gdou.common.domain.model.LoginBody;
import com.gdou.common.utils.MessageUtils;
import com.gdou.common.utils.RedisCache;
import com.gdou.common.utils.StringUtils;
import com.gdou.framework.factory.AsyncFactory;
import com.gdou.framework.security.exception.CaptchaException;
import com.gdou.framework.security.exception.CaptchaExpireException;
import com.gdou.framework.security.handler.LoginFailHandler;
import com.gdou.framework.security.manager.AsyncManager;
import com.gdou.system.service.SysConfigService;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * 在登录校验过滤器 的 验证码校验器
 */
@Component
public class CaptchaFilter extends OncePerRequestFilter {


    @Autowired
    private RedisCache redisCache;

    @Autowired
    private LoginFailHandler loginFailureHandler;

    @Autowired
    private SysConfigService configService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        //查询是否需要验证码
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled && UrlConstants.LOGIN_URL.equals(url) && request.getMethod().equals("POST")) {
            CachedBodyHttpServletRequestWrapper cachedRequest = new CachedBodyHttpServletRequestWrapper(request);

            byte[] bytes = cachedRequest.getInputStream().readAllBytes();
            LoginBody loginBody = JSON.parseObject(bytes, LoginBody.class);
            try {
                validate(loginBody);
            } catch (AuthenticationException e) {
                loginFailureHandler.onAuthenticationFailure(cachedRequest, response, e);
                return;
            }
            filterChain.doFilter(cachedRequest, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 验证码的校验方法
     */
    private void validate(LoginBody loginBody) throws CaptchaException, CaptchaExpireException {
        String uuid = loginBody.getUuid();
        String code = loginBody.getCode();

        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        //验证码正确，删除验证码
        redisCache.deleteObject(CacheConstants.CAPTCHA_CODE_KEY + uuid);
        if (captcha == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginBody.getUsername(), Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException("验证码过期！");
        }
        if (!code.equalsIgnoreCase(captcha)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginBody.getUsername(), Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException("验证码错误！");
        }
    }

    public static class CachedBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] body;

        public CachedBodyHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            this.body = IOUtils.toByteArray(request.getInputStream());
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new CachedBodyServletInputStream(new ByteArrayInputStream(body));
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        public void destroy() throws IOException {
            IOUtils.closeQuietly(getRequest().getInputStream());
        }
    }

    public static class CachedBodyServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public CachedBodyServletInputStream(ByteArrayInputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return inputStream.read(b, off, len);
        }

        @Override
        public int read(byte[] b) throws IOException {
            return inputStream.read(b);
        }
    }


}
