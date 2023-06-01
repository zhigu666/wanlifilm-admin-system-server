package com.system.common.exception;

import com.system.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(value= AccessDeniedException.class)
    public Result handler(AccessDeniedException e) {
//        log.info("spring secutiry权限不足---------{}",e.getMessage());
//        return Result.fail("权限不足");
        throw e;
    }


    @ExceptionHandler(value= IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e){
        log.info("接口参数错误-----------{}",e.getMessage());
        return Result.fail("接口参数错误");
    }

    @ExceptionHandler(value= RuntimeException.class)
    public Result handler(RuntimeException e){
        log.info("运行时异常---------{}",e.getMessage());
//        System.out.println(e);

//        throw e;
        return Result.fail("运行时错误:"+e.getMessage());

    }
}
