package com.system.common;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.service.MenuService;
import com.system.service.RoleMenuService;
import com.system.service.RoleService;
import com.system.service.UserService;
import com.system.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
public class BaseController {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected UserService userService;
    @Autowired
    protected RedisUtil redisUtil;
    @Autowired
    protected MenuService menuService;
    @Autowired
    protected RoleMenuService roleMenuService;
    @Autowired
    protected RoleService roleService;

    public Page getPage(){
        int current= ServletRequestUtils.getIntParameter(request,"current",1);
        int size= ServletRequestUtils.getIntParameter(request,"size",5);
        return new Page(current,size);
    }
}