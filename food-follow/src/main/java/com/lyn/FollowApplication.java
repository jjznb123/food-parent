package com.lyn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.lyn.mapper")
public class FollowApplication {
    public static void main(String[] args) {
        SpringApplication.run(FollowApplication.class,args);
    }
}
