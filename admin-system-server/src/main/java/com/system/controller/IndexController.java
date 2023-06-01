package com.system.controller;

import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Producer;
import com.system.common.BaseController;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.User;
import com.system.entity.dto.MenuDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.sound.sampled.Line;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
//@RequestMapping("/index")
public class IndexController extends BaseController {
    @Autowired
    private Producer producer;

    //获得所有的菜单信息：
    @PreAuthorize("hasAuthority('sys:menu:list')")
    @RequestMapping("/menu/nav")
    public Result nav(Principal principal){
//Principal 是security封装好的对象，直接使用就可以获得用户信息
        String username = principal.getName();
//String username = authentication.getName();
        List<MenuDto> menuDtoList = menuService.getCurrentUserNav(username);
        User user = userService.getUserByUsername(username);
//取权限数据: 取出权限字符串，使用StringUtils.tokenizeToStringArray 按照 逗号 分隔符进行分割，分割成一个字 符串的数组
        String[] authoritys = StringUtils.tokenizeToStringArray(userService.getUserAuthorityInfo(user.getId()), ",");
        return Result.success(MapUtil.builder().put("nav",menuDtoList).put("authoritys",authoritys).map());
    }


    @GetMapping("/captcha")
    public com.system.common.Result captchae() throws IOException {

        //产生验证码字符
        String code=producer.createText();
        //产生一个uuid（一串随机的字符）作为key，b7af2ad9-4be8-49dc-b0fa- 9b4707ddd7b3。 //产生验证码，存储到redis数据库，uuid作为存储的key, 验证码就是这个 key对应存储的value
         String key= UUID.randomUUID().toString();

        //将产生验证码 转为图片 image就是内存中一个图片对象
        BufferedImage image=producer.createImage(code);
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        ImageIO.write(image,"jpg",out);
        System.out.println(1);
        //Base64转码
        BASE64Encoder encoder=new BASE64Encoder();
        String str ="data:image/jpeg;base64,";
        String base64Image =str+=encoder.encode(out.toByteArray());


        //存储redis数据库中： HASH ： 存储 hset key（验证码"captcha"） key(UUID)value(验证码))
        redisUtil.hset(Const.CAPTACHA_KEY,key,code,60);



        //在服务器端输出日志，产生验证码以及存储redis key随机码是多少?
        log.info("验证码--{}--{}",key,code);

        return Result.success(MapUtil.builder().put("key",key).put("captchaImg",base64Image).build());

//        Map map=new HashMap();
//        map.put("key",key);
//        map.put("captchaImg",base64Image);
//        return Result.success(map);
    }
}
