package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.entity.Arrangement;
import com.system.entity.Menu;
import com.system.entity.Orders;
import com.system.service.ArrangementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-05-18
 */
@RestController
@RequestMapping("/system/arrangement")
public class ArrangementController extends BaseController {
    @Autowired
    ArrangementService arrangementService;

    //获得排片信息的列表
    @PreAuthorize("hasAuthority('sys:arrangement:list')")
    @GetMapping("/list")
    //通过用户名查询
    public Result list(String name) {
        Page<Arrangement> arrangement = arrangementService.page(getPage(), new QueryWrapper<Arrangement>().like(StrUtil.isNotBlank(name), "name", name));
        return Result.success(arrangement);
    }

    //删除排片信息的方法：批量删除
    @Transactional
    @PreAuthorize("hasAuthority('sys:arrangement:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] idss) {
//删除用户,接收参数是ids数组，Arrays.asList转换为List集合

        arrangementService.removeByIds(Arrays.asList(idss));
        return Result.success(null);
    }

    //    //保存排片信息
//    @PreAuthorize("hasAuthority('sys:arrangement:save')")
//    @PostMapping("save ")
//    public Result save(@RequestBody Arrangement arrangement){
//
//    }
//根据排片信息的编号 获得该菜单详细信息
    @PreAuthorize("hasAuthority('sys:arrangement:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        Arrangement arrangement = arrangementService.getById(id);
        return Result.success(arrangement);
    }
    //更新排片菜单的数据的方法
    @PreAuthorize("hasAuthority('sys:arrangement:update')")
    @PostMapping("/update")
    public Result update(@RequestBody Arrangement arrangement){
        arrangement.setUpdated(LocalDateTime.now());
        arrangementService.updateById(arrangement);
        return Result.success(arrangement);
    }
    //添加排片菜单信息
    @PreAuthorize("hasAuthority('sys:arrangement:save')")
    @PostMapping("/save")
    public Result save(@RequestBody Arrangement arrangement){
        //保存一个新的菜单，需要设置 该菜单创建时间
        arrangement.setCreated(LocalDateTime.now());
        arrangementService.save(arrangement);
        return Result.success("菜单数据添加成功");
    }
}
