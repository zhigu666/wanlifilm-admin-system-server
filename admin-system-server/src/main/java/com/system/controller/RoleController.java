package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Role;
import com.system.entity.RoleMenu;
import com.system.entity.UserRole;
import com.system.service.RoleMenuService;
import com.system.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhi_lan
 * @since 2023-05-10
 */
@RestController
@RequestMapping("/system/role")
public class RoleController extends BaseController {
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    RoleMenuService roleMenuService;


    @Transactional
    @PreAuthorize("hasAuthority('sys:role:perm')")
    @PostMapping("/perm/{roleId}")
    public Result perm(@PathVariable("roleId") Long reloId,@RequestBody Long[] menuIds){
        List<RoleMenu> roleMenuList=new ArrayList<>();
        Arrays.stream(menuIds).forEach(menu_id->{
            RoleMenu rm=new RoleMenu();
            rm.setRoleId(reloId);
            rm.setMenuId(menu_id);
            roleMenuList.add(rm);
        });

        //现将当前权限数据清空，从sys_role_menu关联表中清空
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id",reloId));
        //将新的权限数据录入
        roleMenuService.saveBatch(roleMenuList);
        //角色权限数据，已经修改，清空redis中和当前角色相关的缓存数据
        userService.clearUserAuthorityInfoByRoleId(reloId);
        return Result.success("");
    }



    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/list")
    public Result list(String name){
        System.out.println(name);
        //搜索栏输入 模糊查询关键词 name
//情况1：name==null没有值， 没有输入模糊查询，查询所有角色分页数据信息：
//情况2： name==XXXX， 输入搜索关键词， 按照 关键词模糊查询 出所有角色的分页数据：
//MP 返回分页数据结果，就是封装到Page对象中，page对象中就包括 records属性（角色分页数据集合） 和相关分页属性 size、total、page...
//select * from role where name like '%XXXXX%'
        Page<Role> roles = roleService.page(getPage(),new QueryWrapper<Role>().like(StrUtil.isNotBlank(name),"name",name));
        return Result.success(roles);
    }



    //编辑角色方法。 请求编辑角色数据，显示对话框，修改对话框，最后更新。
    @PreAuthorize("hasAuthority('sys:role:list')")
    @GetMapping("/roleinfo/{id}")
    public Result info(@PathVariable("id") Long id) {
        Role role = roleService.getById(id);
        System.out.println(1);
//查询该角色 所关联的权限菜单： select menu_id from sys_role_menu where role_id = 角色id
        List<RoleMenu> rolemenu = roleMenuService.list(new QueryWrapper<RoleMenu>().eq("role_id", id));
        System.out.println(2);
        List<Long> menuIds = rolemenu.stream().map(rm -> rm.getMenuId()).collect(Collectors.toList());
        System.out.println(3);
        role.setMenuIds(menuIds);
        return Result.success(role);
    }

    @PreAuthorize("hasAuthority('sys:role:update')")
    @PostMapping("/update")
    public Result update(@RequestBody Role role) {
        role.setUpdated(LocalDateTime.now());
        roleService.updateById(role);
        return Result.success("");
    }


    @PreAuthorize("hasAuthority('sys:role:save')")
    @PostMapping("/save")
    public Result save(@RequestBody Role role) {
        role.setStatu(Const.STATUS_ON);
        role.setCreated(LocalDateTime.now());
        roleService.save(role);
        return Result.success("");
    }


    @Transactional
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
//同步删除其它关联的信息
//因为ids可能是 多个角色编号，不能使用.eq() 应用使用 .in()
        roleMenuService.remove(new QueryWrapper<RoleMenu>().in("role_id",ids));
        userRoleService.remove(new QueryWrapper<UserRole>().in("role_id",ids));
//ids中存储的就是要删除的角色id，根据id删除
//MP 封装的批量删除的方法，removeByIds(集合类型[需要删除的角色id])
//Arrays.asList(数组)
        roleService.removeByIds(Arrays.asList(ids));
        return Result.success("");
    }




}



