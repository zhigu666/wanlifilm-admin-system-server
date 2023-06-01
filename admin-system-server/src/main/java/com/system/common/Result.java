package com.system.common;

import lombok.Data;

@Data
public class Result {
    private int code; //状态码
    private String message; //操作返回信息
    private Object resultdata; //请求响应数据

    //静态方法：创建 请求成功--返回Result对象
    public static Result success(int code, String message, Object data) {
        Result r = new Result();
        r.setCode(code);
        r.setMessage(message);
        r.setResultdata(data);
        return r;
    }


    public static Result success(Object data) {
        return success(20000, "请求操作成功", data);
    }


    //静态方法： 创建 请求失败--返回Result对象
    public static Result fail(int code, String message, Object data) {
        Result r = new Result();
        r.setCode(code);
        r.setMessage(message);
        r.setResultdata(data);
        return r;
    }


    //静态方法：重载fail()方法，返回状态操作失败，返回空数据
    public static Result fail(String message) {
        return fail(400, message, null);
    }
}
