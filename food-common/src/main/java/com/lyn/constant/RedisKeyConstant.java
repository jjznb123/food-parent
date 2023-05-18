package com.lyn.constant;

import lombok.Getter;

/**
redis key 常量类
 */

@Getter
public enum RedisKeyConstant {
    seckill_vouchers("seckill_vouchers","秒杀活动key"),
    seckill_lock("seckill_lock","订单锁");
    private String key;
    private String desc;
    RedisKeyConstant(String key, String desc){
        this.key = key;
        this.desc = desc;
    }






}
