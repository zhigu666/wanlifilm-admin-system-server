package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Category;
import com.system.entity.Role;
import com.system.entity.User;
import com.system.entity.UserRole;
import com.system.entity.dto.PassDto;
import com.system.service.UserRoleService;
import com.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
@RequestMapping("/system/user")
public class UserController extends BaseController {
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    PasswordEncoder passwordEncoder;

    //个人中心中提交个人信息
    @PreAuthorize("hasAuthority('sys:user:myupdate')")
    @PostMapping("/myuser")
    public Result myupdate(@RequestBody User user){
        user.setUpdated(LocalDateTime.now());
        userService.updateById(user);
        return Result.success(null);
    }
    //查看个人中心信息
    @PreAuthorize("hasAuthority('sys:user:mylist')")
    @GetMapping("/myuserinfo/{username}")
    public Result getMyUserByUsername(@PathVariable("username") String username) {
        User user = userService.getUserByUsername(username);
//        if (user != null) {
//            return Result.success(user);
//        } else {
//            return Result.fail("请求用户详细数据失败");
//        }
        return Result.success(user);
    }


//    @PreAuthorize("hasAuthority('sys:user:repass')")
//    @PostMapping("/repass")
//    public Result repass(@RequestBody Long id) {
//        User user = userService.getById(id);
//        String encode_pwd = passwordEncoder.encode(Const.DEFAULT_PASSWORD);
//        user.setPassword(encode_pwd);
//        userService.updateById(user);
//        return Result.success(null);
//    }


    @PreAuthorize("hasAuthority('sys:user:myrepass')")
    @PostMapping("/myrepass")
    public Result myrepass(@RequestBody Long id){
        User user =userService.getById(id);
        user.setPassword(passwordEncoder.encode(Const.DEFAULT_PASSWORD));
        user.setUpdated(LocalDateTime.now());
        userService.updateById(user);
        return Result.success(null);
    }

    @PreAuthorize("hasAuthority('sys:user:repass')")
    @PostMapping("/repass")
    public Result repass(@RequestBody Long id){
        User user =userService.getById(id);
        user.setPassword(passwordEncoder.encode(Const.DEFAULT_PASSWORD));
        user.setUpdated(LocalDateTime.now());
        userService.updateById(user);
        return Result.success(null);
    }

    @PreAuthorize("hasAuthority('sys:user:repass')")
    @PostMapping("/updatePass")
    public Result updatePass(@RequestBody PassDto passDto, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
//修改密码之前，进行密码的验证。旧密码对不对。
        boolean matches = passwordEncoder.matches(passDto.getPassword(), user.getPassword());
//当前修改密码输入的旧密码 和 当前登录的用户的 旧密码是一致的。
        if(!matches){
            return Result.fail("旧密码不正确");
        }
//设置用户的密码为新密码，需要加密
        user.setPassword( passwordEncoder.encode(passDto.getNewPass()) );
        user.setUpdated(LocalDateTime.now());
        userService.updateById(user);
        return Result.success(null);
    }


    @PreAuthorize("hasAuthority('sys:user:role')")
    @PostMapping("/role/{id}")
    public Result userRole(@PathVariable("id") Long id, @RequestBody Long[] roleIds) {
//保存用户角色数据：保存 sys_user_role： 3 , 23,24
//需要将保存用户的id，和保存所有角色id，封装成一个关联表的实体类对象UserRole
        List<UserRole> userRoleList = new ArrayList<>();
        Arrays.stream(roleIds).forEach(r -> {
//r 分配角色id
            UserRole userRole = new UserRole();
            userRole.setUserId(id);
            userRole.setRoleId(r);
            userRoleList.add(userRole); //3 23 ,3 24
        });
//先删除用户现有角色信息
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", id));
//保存用户新分配的角色数据( 可能少了,可能多了 )
        userRoleService.saveBatch(userRoleList);
//删除redis缓存中的用户角色权限数据:
        User user = userService.getById(id);
        userService.clearUserAuthorityINfo(user.getUsername());
        return Result.success(null);
    }




    //获得用户信息的列表
    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/list")
    public Result list(String username) {
//        System.out.println(username);
        Page<User> users = userService.page(getPage(), new QueryWrapper<User>().like(StrUtil.isNotBlank(username), "username", username));
//查询该用户所具有的角色的信息
        users.getRecords().stream().forEach(u -> {
            List<Role> roles = roleService.listRolesByUserId(u.getId());
            u.setRoles(roles);
        });
        return Result.success(users);
    }


    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/userinfo/{username}")
    public Result getUserByUsername(@PathVariable("username") String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.fail("请求用户详细数据失败");
        }
    }
//
//    @PreAuthorize("hasAuthority('sys:user:list')")
//    @GetMapping("/my/{username}")
//    public Result getUserByUsername(@PathVariable("username") String username) {
//        User user = userService.getUserByUsername(username);
//        if (user != null) {
//            return Result.success(user);
//        } else {
//            return Result.fail("请求用户详细数据失败");
//        }
//    }





    //参数传递使用RestFul。
    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/info/{id}")
    public Result getUserById(@PathVariable("id") Long id) {
//根据参数 用户id，查询该用户详细信息
        User user = userService.getById(id);
//根据参数 用户id，查询该用户具备的角色信息 实体类：roles：集合(所有角色)
        List<Role> roles = roleService.listRolesByUserId(id);
        user.setRoles(roles);
//springboot自动将Java对象转换为JSON对象
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.fail("请求用户详细数据失败.");
        }
    }


    //删除用户的方法：批量删除
    @Transactional
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
//删除用户,接收参数是ids数组，Arrays.asList转换为List集合
        userService.removeByIds(Arrays.asList(ids));
//用户和角色关联表中，删除该用户相关的数据 sys_user_role
        userRoleService.remove(new QueryWrapper<UserRole>().in("user_id", ids));
        return Result.success(null);
    }

    //插入新用户信息的方法：
//TODO:添加头像的上传，以及头像图像信息保存
    @PreAuthorize("hasAuthority('sys:user:save')")
    @PostMapping("/save")
    public Result save(@RequestBody User user) {
//录入用户信息，设置当前时间为数据录入时间
        user.setCreated(LocalDateTime.now());
//新创建后台用户，初始默认密码：888888
//默认密码不能明文存储到数据库，需要使用Security的方法进行加密:
        String encode_passowrd = passwordEncoder.encode(Const.DEFAULT_PASSWORD);
        user.setPassword(encode_passowrd);
        if (user.getAvatar()==null || user.getAvatar().equals("")){
            //默认头像：
            user.setAvatar(Const.DEFAULT_AVATAR);
        }

        userService.save(user);
        return Result.success(null);
    }

    //更新用户信息的方法：
//TODO:添加头像的上传，以及头像图像信息保存
    @PreAuthorize("hasAuthority('sys:user:update')")
    @PostMapping("/update")
    public Result update(@RequestBody User user) {
//更新用户信息，更新时间需要设置为当前时间
        user.setUpdated(LocalDateTime.now());
        userService.updateById(user);
        return Result.success(null);
    }

}
