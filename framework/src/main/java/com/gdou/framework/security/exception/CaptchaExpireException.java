package com.gdou.framework.security.exception;

import org.springframework.security.core.AuthenticationException;

public class CaptchaExpireException extends AuthenticationException {
    public CaptchaExpireException(String msg) {
        super(msg);
    }
}
