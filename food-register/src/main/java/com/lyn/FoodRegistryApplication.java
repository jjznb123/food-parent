package com.lyn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Author Liu
 * @Date 2023/4/26 11:49
 * @PackageName:com.lyn
 * @ClassName: FoodRegistryApplication
 * @Version 1.0
 */
@SpringBootApplication
@EnableEurekaServer
public class FoodRegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodRegistryApplication.class,args);
    }
}
