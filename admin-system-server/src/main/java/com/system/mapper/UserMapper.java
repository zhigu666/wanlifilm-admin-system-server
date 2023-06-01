package com.system.mapper;

import com.system.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhi_lan
 * @since 2023-05-10
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    //自定义查询：根据userId用户编号，查询该用户所能操作的菜单id集合。
    public List<Long> getNavMenuIds(Long userId);

    //根据菜单编号menuid查询与该菜单关联的所有用户信息
    public List<User> listByMenuId(Long menuId);
}