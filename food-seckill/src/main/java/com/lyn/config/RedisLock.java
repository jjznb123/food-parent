package com.lyn.config;

import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;
import java.util.UUID;

@Data
public class RedisLock {

    private RedisTemplate redisTemplate;
    private DefaultRedisScript<Long> lockScript;
    private DefaultRedisScript<Object> unlockScript;

    public RedisLock(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        // 构建获取锁的脚本
        this.lockScript = new DefaultRedisScript<>();
        this.lockScript.setScriptSource(
                new ResourceScriptSource(
                        new ClassPathResource("lock.lua")
                )
        );
        this.lockScript.setResultType(Long.class);
        // 构建释放锁的脚本
        this.unlockScript = new DefaultRedisScript<>();
        this.unlockScript.setScriptSource(
                new ResourceScriptSource(
                        new ClassPathResource("unlock.lua")
                )
        );
    }

    /**
     * 获取锁
     */
    public String tryLock(String lockName,long releaseTime){
        String key = UUID.randomUUID().toString();
        Long result = (Long) redisTemplate.execute(
                this.lockScript,
                Collections.singletonList(lockName),
                key + Thread.currentThread().getId(),
                releaseTime
        );
        // 判断结果
        if(result!=null && result.intValue()==1){
            return key;
        }else {
            return null;
        }
    }

    /**
     * 释放锁
     */
    public void unLock(String lockName,String key){
        redisTemplate.execute(
                this.unlockScript,
                Collections.singletonList(lockName),
                key + Thread.currentThread().getId()
        );
    }




}
