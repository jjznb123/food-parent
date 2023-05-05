package com.lyn.config;


import com.lyn.model.domain.SignInIdentity;
import com.lyn.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.LinkedHashMap;

@Configuration
@EnableAuthorizationServer
@Slf4j
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    private final PasswordEncoder passwordEncoder;
    private final RedisTokenStore redisTokenStore;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final ClientOAuth2DataConfigurationProperties clientOAuth2DataConfigurationProperties;


    public AuthorizationServerConfiguration(PasswordEncoder passwordEncoder, RedisTokenStore redisTokenStore, AuthenticationManager authenticationManager, UserService userService, ClientOAuth2DataConfigurationProperties clientOAuth2DataConfigurationProperties) {
        this.passwordEncoder = passwordEncoder;
        this.redisTokenStore = redisTokenStore;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.clientOAuth2DataConfigurationProperties = clientOAuth2DataConfigurationProperties;
    }

    // oauth2/token
    /**
     * 配置令牌端点安全约束
     * RSA：非对称加密算法
     * 公钥：加密 私钥：解密
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //允许访问token的公钥，默认是受保护的
        security.tokenKeyAccess("permitAll()");
        // 允许检查token的状态    oauth/check_token 默认是受保护的
        security.checkTokenAccess("permitAll()");
    }

    /**
     * <h2>客户端授权模型设置</h2>    什么样的客户端才可以访问我
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(clientOAuth2DataConfigurationProperties.getClientId())
                .secret(passwordEncoder.encode(clientOAuth2DataConfigurationProperties.getSecret()))
                .authorizedGrantTypes(clientOAuth2DataConfigurationProperties.getGrantTypes())
                .refreshTokenValiditySeconds(clientOAuth2DataConfigurationProperties.getRefreshTokenValidityTime())
                .accessTokenValiditySeconds(clientOAuth2DataConfigurationProperties.getTokenValidityTime())
                .scopes(clientOAuth2DataConfigurationProperties.getScopes());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.userDetailsService(userService)
                .tokenStore(redisTokenStore)
                .authenticationManager(authenticationManager)
                .tokenEnhancer(((accessToken,authentication) ->{
                    //自定义凭证响应处理
                    SignInIdentity identity=(SignInIdentity) authentication.getPrincipal();
                    LinkedHashMap<String,Object> map=new LinkedHashMap<>();
                    log.info("!!!!!email:[{}]",identity.getEmail());
                    map.put("nickname",identity.getNickname());
                    map.put("email",identity.getEmail());
                    DefaultOAuth2AccessToken token=(DefaultOAuth2AccessToken) accessToken;
                    token.setAdditionalInformation(map);
                    return token;
                }));
    }
}
