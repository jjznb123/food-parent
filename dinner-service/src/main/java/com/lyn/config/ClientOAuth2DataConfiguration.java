package com.lyn.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "client.oauth2")
@Data
public class ClientOAuth2DataConfiguration {
    private String clientId;
    private String secret;
    private String grant_type;
    private String scope;
}
