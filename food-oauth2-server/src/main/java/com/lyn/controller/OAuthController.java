package com.lyn.controller;

import com.lyn.model.common.ResultInfo;
import com.lyn.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author Liu
 * @Date 2023/5/4 14:06
 * @PackageName:com.lyn.controller
 * @ClassName: OAuthController
 * @Version 1.0
 */
@RestController
@RequestMapping("/oauth")
@Slf4j
public class OAuthController {
    @Resource
    private HttpServletRequest request;
    @Resource
    private TokenEndpoint endpoint;

    @PostMapping("/token")
    public ResultInfo postAccessToken(Principal principal,
                                      @RequestParam Map<String,String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken accessToken = endpoint.postAccessToken(principal, parameters).getBody();
        DefaultOAuth2AccessToken token=(DefaultOAuth2AccessToken) accessToken;

        Map<String,Object> data=new LinkedHashMap<>();
        log.info("token:[{}]",token);
        log.info("AdditionalInformation:[{}]",token.getAdditionalInformation());
        log.info("email:[{}]",token.getAdditionalInformation().get("email"));
        data.put("nickname",token.getAdditionalInformation().get("nickname"));
        data.put("email",token.getAdditionalInformation().get("email"));
        data.put("accessToken",token.getValue());
        data.put("refreshToken",token.getRefreshToken());
        data.put("expTime",token.getExpiration());
        return ResultUtil.buildSuccess(request.getServletPath(),data);
    }
}
