package com.lyn;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

@SpringBootTest
@AutoConfigureMockMvc   //Spring Boot Test 专用于测试web请求的工具对象
public class OAuth2ServerApplicationTests {

    // Mock.js---> 测试数据
    @Resource
    protected MockMvc mockMvc;
}
