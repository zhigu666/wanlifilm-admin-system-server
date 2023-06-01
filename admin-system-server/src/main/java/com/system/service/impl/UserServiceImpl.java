package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.entity.Menu;
import com.system.entity.Role;
import com.system.entity.User;
import com.system.mapper.UserMapper;
import com.system.service.MenuService;
import com.system.service.RoleService;
import com.system.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhi_lan
 * @since 2023-05-10
 */

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleService roleService;
    @Autowired
    MenuService menuService;
    @Autowired
    RedisUtil redisUtil;

    //根据用户名查询 该用户的详细信息: 用户名不能重复的
    @Override
    public User getUserByUsername(String username) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("username", username);
        return userMapper.selectOne(qw);
    }

    @Override
    public String getUserAuthorityInfo(Long userId) {
//权限字符串
        String authorityString = "";
//查询用户对象信息， 底层MybatisPlus，ServiceImpl实现类以及实现了IService接口已经实现很多数据获取方法：
        User user = this.getById(userId);
        if (redisUtil.hasKey("granted" + user.getUsername())) {
//判断权限字符串的内容 如果在redis中存在，那么就直接从redis中取。
            authorityString = (String) redisUtil.get("granted" + user.getUsername());
        } else {
//通过登录用户编号UserId获得该用户的所有角色信息
            QueryWrapper role_wrapper = new QueryWrapper();
            role_wrapper.inSql("id", "select role_id from sys_user_role where user_id = " + userId);
            List<Role> roles = roleService.list(role_wrapper);
//通过登录用户编号UserId可以查询该用户所能操作的菜单id
            List<Long> menusIds = userMapper.getNavMenuIds(userId);
//将上面的角色的信息 和 菜单信息 转换为权限字符串( 角色信息和权限信息 ):
//String str ="ROLE_normal,ROLE_admin,sys:manage,sys:user:list"
            if (roles.size() > 0) {
//ROLE_normal,ROLE_admin
                String roleString = roles.stream().map(r ->
                        "ROLE_" + r.getCode()).collect(Collectors.joining(","));
                authorityString = roleString.concat(",");
//ROLE_normal,ROLE_admin,
            }
//使用menusIds 中所有的菜单编号menuId，查询所有的菜单的数据:
            List<Menu> menus = menuService.listByIds(menusIds);
            if (menus.size() > 0) {
//sys:manage,sys:user:list......
                String permString = menus.stream().map(m -> m.getPerms()).collect(Collectors.joining(","));
                authorityString = authorityString.concat(permString);
//ROLE_normal,ROLE_admin,sys:manage,sys:user:list.....
                log.info("权限字符串:{}", authorityString);
            }
//查询出的权限字符串 存储到Redis set(key,字符串)hset(key,Map集合)
            redisUtil.set("granted" + user.getUsername(), authorityString);
        }
        return authorityString;
    }

    //删除掉 redis缓存中某个用户的权限信息
    @Override
    public void clearUserAuthorityINfo(String username) {
        redisUtil.del("granted" + username);
    }

    //删除所有与该角色 关联的用户的权限信息:
    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId) {
//SELECT * FROM sys_user WHERE id in (SELECT user_id FROMsys_user_role WHERE role_id = ?)
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.inSql("id", "SELECT user_id FROM sys_user_role WHERE role_id = " + roleId);
        List<User> users = this.list(qw); //查询出关联到 删除角色的 用户信息
//根据查询出的用户信息，在redis中将这些用户的权限信息删除。
        users.forEach(u -> {
            this.clearUserAuthorityINfo(u.getUsername());
        });
    }

    //删除所有与菜单关联的所有用户的权限信息
    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId) {
        List<User> users = userMapper.listByMenuId(menuId);
//根据查询出的用户信息，在redis中将这些用户的权限信息删除。
        users.forEach(u -> {
            this.clearUserAuthorityINfo(u.getUsername());
        });
    }
}