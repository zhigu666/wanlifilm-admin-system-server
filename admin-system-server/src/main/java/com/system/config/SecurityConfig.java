package com.system.config;

import com.system.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) //开启方法级 权限验证:
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //验证失败的处理器： LoginFailureHandler
    @Autowired
    LoginFailureHandler loginFailureHandler;
    //校验 验证码的过滤器：
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    CaptchaFilter captchaFilter;

    @Autowired
    AuthenticcationEntryPoint authenticcationEntryPoint;
    @Autowired
    JWTAccessDeniedHandler jwtAccessDeniedHandler;
    @Autowired
    JWTLogoutSuccessHandler jwtLogoutSuccessHandler;

    @Bean
    public BCryptPasswordEncoder cryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    JWTAuthenticationFilter jwtAuthenticationFilter() throws Exception{
        return new JWTAuthenticationFilter(authenticationManager());
    }

    //服务器端所有的请求路径：都必须经过SpringSecurity的权限校验，但是有一些基本的路径，不能拦截：例如：登录页面，包括请求验证码 /captcha
//定义一个白名单 列表： 请求的路径只要是 白名单列表中的路径，SpringSecurity放行：
    public static final String[] URL_WHITE_LIST = {
            "/login",
            "/captcha",
            "logout"
    };

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//链式编程：
//关闭csrf()和cors（）
        http.cors().and().csrf().disable()
//登录的配置
                .formLogin()
                .failureHandler(loginFailureHandler) //配置失败处理器对象
                .successHandler(loginSuccessHandler)

                .and()
                .logout()
                .logoutSuccessHandler(jwtLogoutSuccessHandler)

                .and() //SpringSecurity拦截的规则
                .authorizeRequests() //对所有的请求的URL进行SpringSecurity验 证， antMatchers()请求放行的规则，采用白名单
                .antMatchers(URL_WHITE_LIST).permitAll() //permitAll() 对 于所有人都采用这些规则
                .anyRequest().authenticated() //anyRequest() 表示匹配任意 的URL请求，authenticated() 任何请求都必须被 SpringSecurity验证 后才可访 问。


                .and()//因为采用的是前后端分离开发，不使用session技术，禁用 session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//SessionCreationPolicy.STATELESS SpringSecurity永远不会创建session对象，不会使用HttpSession创建session
//SessionCreationPolicy.ALWAYS 总是创建HttpSession
//SessionCreationPolicy.IF_REQUIRED SpringSecurity只会在需要时创建一个HttpSession
        //SessionCreationPolicy.NEVER SpringSecurity不会创建session对象，但是如果已经存在session，是可以使用session的。
//配置自定义过滤器



                //配置异常处理器
                .and()
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler) //权限不足的处理器
                .authenticationEntryPoint(authenticcationEntryPoint) //Token验证失败 的处理器

                //配置自定义过滤器
                .and() //在SpringSecurity 验证 用户名和密码的过滤器执行之前，执行我们 自定义的captchaFilter验证码过滤器
                .addFilter(jwtAuthenticationFilter()) //配置jwt验证过滤器
                .addFilterBefore(captchaFilter,UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
