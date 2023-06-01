package com.system.service;

import com.system.entity.Film;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhi_lan
 * @since 2023-05-28
 */
public interface FilmService extends IService<Film> {
    public Film getFilmNameByFid(Long fid);
}
