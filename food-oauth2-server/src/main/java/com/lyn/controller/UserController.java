package com.lyn.controller;

import cn.hutool.core.bean.BeanUtil;
import com.lyn.model.common.ResultInfo;
import com.lyn.model.domain.SignInDinerInfo;
import com.lyn.model.domain.SignInIdentity;
import com.lyn.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {


    @Resource
    private HttpServletRequest request;

    @Resource
    private RedisTokenStore redisTokenStore;

    // SecurityContext   user/me --->  AuthenticationFilter ---> authentication->securityContext
    // authentication [p:userDetails  c: null]
    @GetMapping("/me")
    public ResultInfo getCurrentUser(Authentication authentication){
        SignInIdentity signInIdentity = (SignInIdentity) authentication.getPrincipal();
        SignInDinerInfo dinerInfo = new SignInDinerInfo();
        BeanUtil.copyProperties(signInIdentity,dinerInfo);
        return ResultUtil.buildSuccess(request.getServletPath(),dinerInfo);
    }

// 918530eb-c336-4564-ab08-87978a3826ae
    @GetMapping("/logout")
    public ResultInfo logout(String access_token){
        log.info("authorization: [{}]",request.getHeader("Authorization"));
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isBlank(access_token)){
            // 如果access_token为空 将 authorization复制给他
            access_token = authorization;
        }
        if(StringUtils.isBlank(access_token)){
            return ResultUtil.buildError(request.getServletPath());
        }

        if(access_token.toLowerCase(Locale.ROOT).contains("bearer ".toLowerCase(Locale.ROOT))){
            access_token = access_token.toLowerCase(Locale.ROOT).replace("bearer ","");
        }

        OAuth2AccessToken oAuth2AccessToken = redisTokenStore.readAccessToken(access_token);
        if(oAuth2AccessToken!=null){
            redisTokenStore.removeAccessToken(oAuth2AccessToken);
            OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
            redisTokenStore.removeRefreshToken(refreshToken);
        }
        return ResultUtil.buildSuccess(request.getServletPath(),"退出成功！");

    }
}
