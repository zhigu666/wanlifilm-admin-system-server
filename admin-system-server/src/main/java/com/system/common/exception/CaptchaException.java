package com.system.common.exception;

import org.springframework.security.core.AuthenticationException;
public class CaptchaException extends AuthenticationException {
    //自定义 验证码 失败异常类,msg参数就是验证失败的信息提示：
    public CaptchaException(String msg) {
        super(msg);
    }
}