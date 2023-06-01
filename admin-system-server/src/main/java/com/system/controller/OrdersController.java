package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.entity.Orders;
import com.system.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.util.Arrays;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-05-18
 */
@RestController
@RequestMapping("/system/order")
public class OrdersController extends BaseController {
    @Autowired
    OrdersService ordersService;
    //获得订单信息的列表
    @PreAuthorize("hasAuthority('sys:order:list')")
    @GetMapping("/list")
    //通过用户名查询
    public Result list(String username) {
        Page<Orders> orders = ordersService.page(getPage(), new QueryWrapper<Orders>().like(StrUtil.isNotBlank(username), "username", username));
        return Result.success(orders);
    }

    //删除订单的方法：批量删除
    @Transactional
    @PreAuthorize("hasAuthority('sys:order:delete')")
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
//删除用户,接收参数是ids数组，Arrays.asList转换为List集合
        ordersService.removeByIds(Arrays.asList(ids));
        return Result.success(null);
    }

}
