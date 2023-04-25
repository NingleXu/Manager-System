package com.gdou.framework.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 *
 *  缓存错误类
 */
public class CaptchaException extends AuthenticationException {
    public CaptchaException(String msg){
        super(msg);
    }
}
