package com.lyn.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private HandleException handleException;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 处理url地址，判断是否在白名单中
        AntPathMatcher pathMatcher = new AntPathMatcher();
        boolean flag = false;
        String path = exchange.getRequest().getURI().getPath();
        for(String patten: ignoreUrlsConfig.getUrls()){
            if(pathMatcher.match(patten,path)){
                flag = true;
                break;
            }
        }
        if(flag){
            // 放行请求
            return chain.filter(exchange);
        }

        // 处理token
        // 1. 获取token
        String token =
                exchange.getRequest().getQueryParams().getFirst("access_token");
        if(StringUtils.isBlank(token)){
            // 给予提示，并返回错误
            handleException.writeError(exchange,"访问令牌不存在！");
        }
        //OAuth2标准下，用于校验的凭证的接口， oauth/check_token
        String checkUrl = "http://food-oauth2-server/oauth/check_token?token="+token;
        // 发送远程请求，验证token是否有效
        try{
            ResponseEntity<String> entity = restTemplate.getForEntity(checkUrl, String.class);
            if(entity.getStatusCode() != HttpStatus.OK){
                return handleException.writeError(exchange,"令牌无效！token: "+token);
            }
            if(StringUtils.isBlank(entity.getBody())){
                return handleException.writeError(exchange,"令牌无效！token: "+token);
            }
        }catch (Exception e){
            return handleException.writeError(exchange,e.getMessage());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0+HIGHEST_PRECEDENCE;
    }
}
