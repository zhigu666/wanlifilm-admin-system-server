package com.system.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.common.Result;
import com.system.entity.Menu;
import com.system.entity.RoleMenu;
import com.system.entity.User;
import com.system.entity.dto.MenuDto;
import com.system.service.MenuService;
import com.system.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import
        org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.system.common.BaseController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhi_lan
 * @since 2023-05-10
 */
@RestController
@RequestMapping("/system/menu")
public class MenuController extends BaseController {

    @Autowired
    RoleMenuService roleMenuService;

    //根据菜单的编号 获得该菜单详细信息
    @PreAuthorize("hasAuthority('sys:menu:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        Menu menu = menuService.getById(id);
        return Result.success(menu);
    }

    //更新菜单的数据方法
    @PreAuthorize("hasAuthority('sys:menu:update')")
    @RequestMapping("/update")
    public Result update(@RequestBody Menu menu) {
        //修改更新时间为当前时间
        menu.setUpdated(LocalDateTime.now());
        menuService.updateById(menu);
        return Result.success(menu);
    }

    //sys:menu:delete 自定义 menu:delete
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    @RequestMapping("/delete/{id}")
    public Result del(@PathVariable("id") Long id) {
        //查询要删除的菜单id
        int count = menuService.count(new QueryWrapper<Menu>().eq("parent_id", id));
        if (count > 0) {
            return Result.fail("请先删除子菜单");
        }
        //清空redis中用户权限以及当前删除菜单id关联的数据
        userService.clearUserAuthorityInfoByMenuId(id);
        menuService.removeById(id);
        //删除菜单,sys_role_menu中还有角色和菜单关联的数据需要删除
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("menu_id", id));

        return Result.success("");
    }

    //获得菜单页面加载时 表格的菜单数据：
    @PreAuthorize("hasAuthority('sys:menu:list')")
    @RequestMapping("/list")
    public Result list() {
        List<Menu> tree = menuService.tree();
        return Result.success(tree);
    }

    //获得所有的菜单信息：
//    @PreAuthorize("hasAuthority('sys:menu:list')")
//    @RequestMapping("/nav")
//    public Result nav(Principal principal){
////Principal 是security封装好的对象，直接使用就可以获得用户信息
//        String username = principal.getName();
////String username = authentication.getName();
//        List<MenuDto> menuDtoList = menuService.getCurrentUserNav(username);
//        User user = userService.getUserByUsername(username);
////取权限数据: 取出权限字符串，使用StringUtils.tokenizeToStringArray 按照 逗号 分隔符进行分割，分割成一个字符串的数组
//        String[] authoritys = StringUtils.tokenizeToStringArray(userService.getUserAuthorityInfo(user.getId()), ",");
//        return Result.success(MapUtil.builder().put("nav",menuDtoList).put("authoritys",authoritys).map());
//    }


    //添加菜单信息
    @PreAuthorize("hasAuthority('sys:menu:save')")
    @PostMapping("/save")
    public Result save(@RequestBody Menu menu){
//保存一个新的菜单，需要设置 该菜单创建时间
        menu.setCreated(LocalDateTime.now());
        menuService.save(menu);
        return Result.success("菜单数据添加成功");
    }


}
