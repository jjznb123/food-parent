package com.lyn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Author Liu
 * @Date 2023/4/25 10:29
 * @PackageName:com.lyn
 * @ClassName: DinnerApplication
 * @Version 1.0
 */
@SpringBootApplication
@EnableEurekaClient
public class DinnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DinnerApplication.class,args);
    }
}
