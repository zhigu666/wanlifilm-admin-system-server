package com.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.entity.Role;
import com.system.entity.User;
import com.system.mapper.RoleMapper;
import com.system.mapper.UserMapper;
import com.system.service.UserService;
import org.apache.coyote.OutputBuffer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class AdmainSystemServerApplicationTests {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleMapper roleMapper;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserService userService;


    @Test
    public void permsTest(){
        String s=userService.getUserAuthorityInfo(2L);
        System.out.println(s);
    }



    @Test
    public void laTest(){
        List list=new ArrayList();
    }

    @Test
    public void testPassword(){
        String pwd="123";
        String encode = bCryptPasswordEncoder.encode(pwd);
        System.out.println(encode);

        String inputPwd ="123";

        System.out.println(bCryptPasswordEncoder.matches(inputPwd,encode));
    }

    @Test
    public void testSelect() {
//查询所有用户
        List<User> users = userMapper.selectList(null);
        for (User u : users) {
            System.out.println(u);
        }
    }

    @Test
    public void testSelectById() {
        User user = userMapper.selectById(9);
        System.out.println(user);
//插入一个新的用户。
        user.setUsername("zhilan");
        user.setPassword("admin");
        user.setId(null); //数据插入，注意数据库表中id的主键策略，如果是自增，不要录入id数据
        int result = userMapper.insert(user);
        System.out.println(result); //执行影响行数。
    }

    @Test
    public void testUpdate() {
        Role r = new Role();
        r.setId(3L);
        r.setName("WWWW");
        r.setCode("222");
        r.setStatu(1);
        int i = roleMapper.updateById(r);
        System.out.println(i);
    }

    @Test
    public void testDelete() {
        int i = userMapper.deleteById(9);
        System.out.println(9);
    }

    @Test
    public void testQueryWrapper(){
        QueryWrapper qw = new QueryWrapper();
//select * from user where id > 9
        qw.gt("id",9); //where id > 9 and XXXX=X
        qw.lt("id",20); //where id >9 and id <20
        List list = userMapper.selectList(qw);
        System.out.println(list);
    }


    @Test
    public void fun() throws UnsupportedEncodingException {
        BASE64Encoder encoder =new BASE64Encoder();
        String encode = encoder.encode("zhilan".getBytes("utf-8"));
        System.out.println(encode);
    }

}