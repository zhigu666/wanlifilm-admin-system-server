package com.system.security;


import cn.hutool.json.JSONUtil;
import com.system.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//权限不足的处理器：
@Slf4j
@Component
public class JWTAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        System.out.println("2222");
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream out = response.getOutputStream();
        log.info("--用户权限不足--");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); ///403
        Result result = Result.fail(400,"权限不足:"+e.getMessage(),"null");
        out.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}
