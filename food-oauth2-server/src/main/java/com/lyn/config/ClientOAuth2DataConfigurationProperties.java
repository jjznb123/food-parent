package com.lyn.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Liu
 * @Date 2023/4/26 15:13
 * @PackageName:com.lyn.config
 * @ClassName: ClientOAuth2DataConfigurationProperties
 * @Version 1.0
 */
@Component
@ConfigurationProperties(prefix = "client.oauth2")
@Getter
@Setter
public class ClientOAuth2DataConfigurationProperties {
    // 客户端标识ID
    private String clientId;
    // 客户端安全码
    private String secret;
    //授权类型
    private String[] grantTypes;
    // token有效期
    private int tokenValidityTime;
    //refreshToken 有效期
    private int refreshTokenValidityTime;
    // 客户端访问范围
    private String[] scopes;
}
