package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Fans;
import com.system.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhi_lan
 * @since 2023-05-23
 */
@RestController
@RequestMapping("/system/fans")
public class FansController extends BaseController {
    @Autowired
    FansService fansService;
    @Autowired
    PasswordEncoder passwordEncoder;
    //获得所有粉丝数据，分页
    @PreAuthorize("hasAuthority('sys:fans:list')")
    @GetMapping("list")
    public Result list(String name){
        Page<Fans> fans=fansService.page(getPage(),new QueryWrapper<Fans>().like(StrUtil.isNotBlank(name),"username",name));
        return Result.success(fans);
    }
    //通过粉丝id，查询该类别的详细信息
    @PreAuthorize("hasAuthority('sys:fans:list')")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        Fans fans=fansService.getById(id);
        return Result.success(fans);
    }
    //添加粉丝信息，保存
    @PreAuthorize("hasAuthority('sys:fans:save')")
    @PostMapping("/save")
    public Result save(@RequestBody Fans fans){
        fans.setCreated(LocalDateTime.now());

        String encode_passowrd = passwordEncoder.encode(Const.DEFAULT_PASSWORD);
        fans.setPassword(encode_passowrd);
        if (fans.getAvatar()==null || fans.getAvatar().equals("")){
            //默认头像：
            fans.setAvatar(Const.DEFAULT_AVATAR);
        }

        System.out.println("凄凄切切群群群群save");
        fans.setStatu(Const.STATUS_ON);
        fansService.save(fans);
        return Result.success(null);
    }
    //更新粉丝信息，更新
    @PreAuthorize("hasAuthority('sys:fans:update')")
    @PostMapping("/update")
    public Result update(@RequestBody Fans fans){

        fans.setUpdated(LocalDateTime.now());
        System.out.println("哦哦哦哦哦哦哦哦哦哦哦哦update");
        fansService.updateById(fans);
        return Result.success(null);
    }
    //删除粉丝信息，删除
    @PreAuthorize("hasAuthority('sys:fans:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        fansService.removeByIds(Arrays.asList(ids));
        return Result.success(null);
    }
    //重置粉丝密码
    @PreAuthorize("hasAuthority('sys:fans:repass')")
    @PostMapping("/repass")
    public Result repass(@RequestBody Long id){
        Fans fans =fansService.getById(id);
        fans.setPassword(passwordEncoder.encode(Const.DEFAULT_PASSWORD));
        fans.setUpdated(LocalDateTime.now());
        fansService.updateById(fans);
        return Result.success(null);
    }
}
