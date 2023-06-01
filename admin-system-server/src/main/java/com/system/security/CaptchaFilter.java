package com.system.security;

import com.system.common.exception.CaptchaException;
import com.system.common.lang.Const;
import com.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@Component
public class CaptchaFilter extends OncePerRequestFilter {
    private final String loginURL = "/login";
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LoginFailureHandler loginFailureHandler; //自动装配 验证失败处理器的对象
    //具体校验验证码的方法：
    private void validate(HttpServletRequest request) throws CaptchaException{
/*登录请求，前端请求登录参数：
loginForm: {
username: '',
password: '',
code: '', //验证码 存redis中的value
key:'' //随机码 存redis中的key
}
this.$axios.post('/api/login',
this.$qs.stringify(this.loginForm)) $qs.stringify对象将 loginForm 转换
为参数字符串形式:
username=XXX&password=XXX&code=XXXXX&key=XXXX 这个字符串格
式参数，通过http( 参数主体部分 )的POST方式传递。
*/
        String code = request.getParameter("code"); //获得前端登录输入验证码
        String key = request.getParameter("key"); //获得刷新登录页面，显示验证码时 服务器端返回key值。
//commons-lang3工具类，判断字符对象是不是空的方法，如果是空 true，不是空 false isBlank(对象)
        if(StringUtils.isBlank(code) || StringUtils.isBlank(key) ){
//如果请求的code验证码 或者 验证码存储key 有一个是空的，抛出异常。 抛出异常 抛给SrpingSecurity。
//自定义异常：验证码失败异常：
            throw new CaptchaException("验证码不能为空");
        }
        log.info("用户输入的验证码{}----redis读取出验证码 {}",code,redisUtil.hget(Const.CAPTACHA_KEY,key));
        if(!code.equals(redisUtil.hget(Const.CAPTACHA_KEY,key))){
            throw new CaptchaException("验证码不正确");
        }
//后面代码，验证码验证成功: 验证码都是一次性,要从redis中将验证码删除
        redisUtil.hdel(Const.CAPTACHA_KEY,key);
    }
    //过滤器主要过滤代码的方法
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException,ServletException {
//获得请求url
        String uri = request.getRequestURI();
//判断请求该过滤器 请求路径是不是/login，请求的方式是不是使用POST方式
        if(loginURL.equals(uri) && request.getMethod().equals("POST"))
        {
            log.info("login请求链接，正在校验验证码---"+uri);
//校验验证码
            try {
                this.validate(request);
            } catch (CaptchaException e) {
                log.info(e.getMessage());
//验证失败：调用验证失败处理器：
                loginFailureHandler.onAuthenticationFailure(request,response,e);
            }
        }
//验证成功，过滤器跳转至 后面的资源： SpringSecurity验证用户名和密码
        filterChain.doFilter(request,response);
    }
}