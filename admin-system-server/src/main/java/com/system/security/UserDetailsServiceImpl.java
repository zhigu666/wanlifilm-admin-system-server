package com.system.security;

import com.system.entity.User;
import com.system.service.UserService;
import com.system.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.LongSeqHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.TreeSet;


@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserService userService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserDetailsServiceImpl userDetailsService;


    @Override
    public AccountUser loadUserByUsername(String username) throws UsernameNotFoundException {
//使用这个用户，查询该用户的详细的用户信息。 User
        User user = userService.getUserByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("用户名或密码不存在");
        }
        AccountUser accountUser = new AccountUser(user.getId(), user.getUsername(), user.getPassword(),getUserAuthority(user.getId())); //调用下面的方法，获得权限
        System.out.println(accountUser.getPassword());
        return accountUser;
    }
    //获取权限信息的方法 返回List<GrantedAuthority>是seucrity识别类型
    public List<GrantedAuthority> getUserAuthority(Long userId){
//获得该用户的权限字符串
        String userAuthorityString = userService.getUserAuthorityInfo(userId);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(userAuthorityString);
    }

}
