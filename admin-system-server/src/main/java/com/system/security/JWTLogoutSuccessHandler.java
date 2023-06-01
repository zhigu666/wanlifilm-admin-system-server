package com.system.security;

import cn.hutool.json.JSONUtil;
import com.system.common.Result;
import com.system.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;


@Component
public class JWTLogoutSuccessHandler implements LogoutSuccessHandler {
    @Autowired
    JwtUtil jwtUtil;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication !=null){
            new SecurityContextLogoutHandler().logout(request,response,authentication)
            ;
        }
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream out = response.getOutputStream();
//登出操作成功，需要清空 前端存储token。
        response.setHeader(jwtUtil.getHeader(),"");
        Result result = Result.success("登出操作成功");
        out.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}