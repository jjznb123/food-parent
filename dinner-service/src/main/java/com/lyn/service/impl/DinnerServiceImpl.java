package com.lyn.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.lyn.config.ClientOAuth2DataConfiguration;
import com.lyn.constant.ApiConstant;
import com.lyn.model.common.ResultInfo;
import com.lyn.model.domain.OAuthDinnerInfo;
import com.lyn.service.IDinnerService;
import com.lyn.util.AssertUtil;
import com.lyn.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.LinkedHashMap;

@Service
@Slf4j
public class DinnerServiceImpl implements IDinnerService {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ClientOAuth2DataConfiguration clientOAuth2DataConfiguration;

    @Override
    public ResultInfo login(String username, String password, String path) {
        //校验参数
        AssertUtil.isNotNull(username,"请输入用户名");
        AssertUtil.isNotEmpty(password,"请输入密码");

        //构建header
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //构建body
        MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();
        body.add("username",username);
        body.add("password",password);
        body.setAll(BeanUtil.beanToMap(clientOAuth2DataConfiguration));
        //构建entity
        HttpEntity<MultiValueMap<String,Object>> entity=new HttpEntity<>(body,headers);
        //设置authorization
        restTemplate.getInterceptors().add(
                new BasicAuthenticationInterceptor(
                        clientOAuth2DataConfiguration.getClientId(),
                        clientOAuth2DataConfiguration.getSecret()
                )
        );
        ResultInfo resultInfo=restTemplate.postForObject(
                "http://food-oauth2-server/oauth/token",
                entity,
                ResultInfo.class
        );
        // 校验响应结果
        // 失败！
        if(resultInfo.getCode() != ApiConstant.SUCCESS_CODE){
            resultInfo.setData(resultInfo.getMessage());
            return resultInfo;
        }
        OAuthDinnerInfo dinnerInfo = new OAuthDinnerInfo();
        BeanUtil.fillBeanWithMap((LinkedHashMap<String, Object>) resultInfo.getData(),dinnerInfo,false);
        return ResultUtil.buildSuccess(path,dinnerInfo);
    }
}
