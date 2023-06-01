package com.system.service;

import com.system.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhi_lan
 * @since 2023-05-10
 */
public interface UserService extends IService<User> {
    //根据用户名查询 该用户的详细信息
    public User getUserByUsername(String username);
    //根据用户ID获得该用户相关权限列表信息
    public String getUserAuthorityInfo(Long userId);
//------------------添加的2种操作:redis中权限数据------------------------
    //删除某个用户的缓存中的权限信息
    public void clearUserAuthorityINfo(String username);
    //删除所有与该角色 关联的用户的权限信息:
    public void clearUserAuthorityInfoByRoleId(Long roleId);
    //删除所有与菜单关联的所有用户的权限信息
    public void clearUserAuthorityInfoByMenuId(Long menuId);
}
