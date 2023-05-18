package com.lyn.controller;

import com.lyn.model.common.ResultInfo;
import com.lyn.model.pojo.SeckillVouchers;
import com.lyn.service.SeckillVoucherService;
import com.lyn.util.ResultUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Resource
    private HttpServletRequest request;

    @Resource
    private SeckillVoucherService seckillVoucherService;
    @PostMapping("/add")
    public ResultInfo<String> addSeckillVouchers(@RequestBody SeckillVouchers seckillVouchers){
        seckillVoucherService.addSeckillVouchers(seckillVouchers);
        return ResultUtil.buildSuccess(
                request.getServletPath(),
                "添加成功"
        );
    }


    @PostMapping("/doSeckill")
    public ResultInfo<String> doSeckill(Integer voucherId,String access_token){
        return seckillVoucherService.stockDec(access_token,voucherId,request.getServletPath());
    }
}
