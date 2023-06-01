package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.*;
import com.system.mapper.FilmMapper;
import com.system.service.FilmEvaluateService;
import com.system.service.FilmService;
import com.system.utils.OSSUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhi_lan
 * @since 2023-05-28
 */
@RestController
@RequestMapping("/system/evaluate")
public class FilmEvaluateController extends BaseController {
    @Autowired
    FilmEvaluateService filmEvaluateService;
    @Autowired
    FilmService filmService;



    //通过用户名、电影名、评论查询list
    @PreAuthorize("hasAuthority('sys:evaluate:list')")
    @GetMapping("/list")
    public Result list(String name,String filmname,String evaluatename) {

        QueryWrapper<Film> thisfilmq=new QueryWrapper<Film>().like(StrUtil.isNotBlank(filmname),"name",filmname);
        List<Film> filmList= filmService.list(thisfilmq);
        List<Long> list=new ArrayList<>();
        filmList.forEach(r->{
            list.add(r.getId());
        });
//        System.out.println(list.toString());
//        String lists= StringUtils.strip(list.toString(),"[]");

//        if (list==null)
        System.out.println(list);
        Page<FilmEvaluate> filmEvaluate= filmEvaluateService.page(getPage(), new QueryWrapper<FilmEvaluate>()
                .like(StrUtil.isNotBlank(evaluatename), "comment", evaluatename)
                .like(StrUtil.isNotBlank(name), "username", name)
                .in(!list.isEmpty(),"fid",list)
                .eq(list.isEmpty(),"1",0)
        );
//        filmEvaluate.getRecords().stream().forEach(u->{
//            Film film=filmService.getFilmNameByFid(u.getFid());
//            System.out.println(film);
//            u.setFname(film.getName());
//        });
        return Result.success(filmEvaluate);
    }
    //删除评论：批量删除delete
    @PreAuthorize("hasAuthority('sys:evaluate:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        filmEvaluateService.removeByIds(Arrays.asList(ids));
        return Result.success(null);
    }
    //更新评论信息update
    @PreAuthorize("hasAuthority('sys:evaluate:update')")
    @PostMapping("/update")
    public Result update(@RequestBody FilmEvaluate filmEvaluate){
        filmEvaluate.setUpdated(LocalDateTime.now());
        filmEvaluateService.updateById(filmEvaluate);
        return Result.success(null);
    }
    //新建评论save
    @PreAuthorize("hasAuthority('sys:evaluate:save')")
    @PostMapping("/save")
    public Result save(@RequestBody FilmEvaluate filmEvaluate){
        filmEvaluate.setCreated(LocalDateTime.now());
        filmEvaluate.setStatu(Const.STATUS_ON);
        filmEvaluateService.save(filmEvaluate);
        return Result.success(null);
    }
    //根基评论id获取详细信息info
    @PreAuthorize("hasAuthority('sys:evaluate:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        FilmEvaluate filmEvaluate=filmEvaluateService.getById(id);
        return Result.success(filmEvaluate);
    }
}
