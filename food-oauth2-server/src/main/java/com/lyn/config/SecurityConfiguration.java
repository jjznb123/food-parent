package com.lyn.config;

import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;


// Security配置类
@Configuration
@EnableWebSecurity // 启用WebSecurity支持 ，可以省略，Starter会自动加载
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    // 放行和认证规则，放行 oauth/**
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                // /actuator/**: 监控端点，SpringBoot Actuator 可以自动给工程添加很多web端点用于监控项目状态
                // Spring-boot-starter-actuator  8080  localhost:8080/info
                // localhost:8080/health
                .antMatchers("/oauth/**","/actuator/**").permitAll()
                .anyRequest()
                .authenticated();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return DigestUtil.md5Hex(charSequence.toString());
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return DigestUtil.md5Hex(charSequence.toString()).equalsIgnoreCase(s);
            }
        };
    }

    @Bean
    public RedisTokenStore redisTokenStore(RedisConnectionFactory redisConnectionFactory){
        RedisTokenStore redisTokenStore=new RedisTokenStore(redisConnectionFactory);
        redisTokenStore.setPrefix("TOKEN:");
        return redisTokenStore;
    }
}
