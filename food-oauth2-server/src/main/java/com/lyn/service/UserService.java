package com.lyn.service;

import cn.hutool.core.bean.BeanUtil;
import com.lyn.mapper.UserMapper;
import com.lyn.model.domain.SignInIdentity;
import com.lyn.model.pojo.Diners;
import com.lyn.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author Liu
 * @Date 2023/4/26 15:16
 * @PackageName:com.lyn.service
 * @ClassName: UserService
 * @Version 1.0
 */
@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //校验参数
        AssertUtil.isNotEmpty(s, "请输入用户名");
        Diners diners = userMapper.findByUsername(s);
        if (diners == null) {
            throw new UsernameNotFoundException("用户不存在！！");
        }
        SignInIdentity signInIdentity = new SignInIdentity();
        //工具类，属性拷贝
        BeanUtil.copyProperties(diners, signInIdentity);

        return signInIdentity;
    }
}
