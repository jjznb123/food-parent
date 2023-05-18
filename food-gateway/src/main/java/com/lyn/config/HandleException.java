package com.lyn.config;

import com.alibaba.fastjson.JSON;
import com.lyn.constant.ApiConstant;
import com.lyn.model.common.ResultInfo;
import com.lyn.util.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class HandleException {

    public Mono<Void> writeError(ServerWebExchange exchange,String error){
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        // 输出错误信息
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResultInfo resultInfo = ResultUtil.buildError(
                ApiConstant.NO_LOGIN_CODE,
                StringUtils.isBlank(error)?ApiConstant.NO_LOGIN_MESSAGE:error,
                request.getURI().getPath()
        );
        DataBuffer buffer = response.bufferFactory().wrap(JSON.toJSONString(resultInfo).getBytes(StandardCharsets.UTF_8));
        // 向客户端输出resultInfo对象
        return response.writeWith(Mono.just(buffer));
    }
}
