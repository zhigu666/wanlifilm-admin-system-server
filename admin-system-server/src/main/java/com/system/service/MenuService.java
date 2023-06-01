package com.system.service;

import com.system.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.entity.dto.MenuDto;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhi_lan
 * @since 2023-05-10
 */
public interface MenuService extends IService<Menu> {

    public List<MenuDto> getCurrentUserNav(String username);
//    public List<Menu> buildTreeMenu(List<Menu> menus);
    //获得菜单首页数据，返回一个父子关系的菜单集合
    public List<Menu> tree();

}
