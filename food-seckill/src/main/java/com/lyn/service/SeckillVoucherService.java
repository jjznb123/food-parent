package com.lyn.service;

import com.lyn.model.common.ResultInfo;
import com.lyn.model.pojo.SeckillVouchers;

public interface SeckillVoucherService {

    // 添加活动
    void addSeckillVouchers(SeckillVouchers seckillVouchers);

    ResultInfo stockDec(String access_token,Integer voucherId,String path);
}
