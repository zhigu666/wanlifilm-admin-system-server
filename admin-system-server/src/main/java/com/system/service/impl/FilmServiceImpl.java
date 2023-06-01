package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.entity.Film;
import com.system.entity.UserRole;
import com.system.mapper.FilmMapper;
import com.system.service.FilmService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhi_lan
 * @since 2023-05-28
 */
@Service
public class FilmServiceImpl extends ServiceImpl<FilmMapper, Film> implements FilmService {
    @Autowired
    FilmService filmService;

    @Autowired
    UserRoleService userRoleService;
    @Override
    public Film getFilmNameByFid(Long fid) {

        Film film=this.getById(fid);
        return film;
    }
}
