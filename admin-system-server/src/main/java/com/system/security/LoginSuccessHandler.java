package com.system.security;

import cn.hutool.json.JSONUtil;
import com.system.common.Result;
import com.system.entity.User;
import com.system.service.UserService;
import com.system.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserService userService;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream out=response.getOutputStream();
        //产生令牌token ,token返回vue端
        String username = authentication.getName();
        String token = jwtUtil.createToken(username);

        //响应token的同时，将登录用户成功的信息一起响应，需要username查询到具体的用户信息
        //将token挂到响应头
        response.setHeader(jwtUtil.getHeader(),token);
        User user=userService.getUserByUsername(username);

        Result result =Result.success(user);
        //登录校验成功，返回一个成功否result对象
        out.write(JSONUtil.toJsonStr(result).getBytes("utf-8"));
        out.flush();
        out.close();
    }
}
