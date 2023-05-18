package com.lyn.controller;


import com.lyn.model.common.ResultInfo;
import com.lyn.service.IDinnerService;
import com.lyn.util.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/dinner")
public class DinnerController {

    @Resource
    private IDinnerService dinnerService;

    @Resource
    private HttpServletRequest request;

    @PostMapping("/login")
    public ResultInfo login(String username, String password){
        return dinnerService.login(username,password,request.getServletPath());
    }
    @GetMapping("/test")
    public ResultInfo test(){
        return ResultUtil.buildSuccess(request.getServletPath());
    }
}
