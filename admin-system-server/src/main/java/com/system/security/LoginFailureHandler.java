package com.system.security;

import cn.hutool.json.JSONUtil;
import com.system.common.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//登录验证失败的处理器：
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    //登录验证失败的操作代码，写这个方法里：
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ServletOutputStream out = response.getOutputStream();
//请求失败参数对象： 封装Result
        Result result = Result.fail("登录验证失败"); //Result是一个Java，输出前端需要转换为JSON。
        out.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        out.flush();
        out.close();
        ;
    }
}