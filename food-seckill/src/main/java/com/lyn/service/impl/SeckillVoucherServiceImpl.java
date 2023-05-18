package com.lyn.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.lyn.config.RedisLock;
import com.lyn.constant.ApiConstant;
import com.lyn.constant.RedisKeyConstant;
import com.lyn.exception.ParameterException;
import com.lyn.mapper.SeckillOrderMapper;
import com.lyn.mapper.SeckillVoucherMapper;
import com.lyn.model.common.ResultInfo;
import com.lyn.model.domain.SignInDinerInfo;
import com.lyn.model.pojo.SeckillVouchers;
import com.lyn.model.pojo.VoucherOrders;
import com.lyn.service.SeckillVoucherService;
import com.lyn.util.AssertUtil;
import com.lyn.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class SeckillVoucherServiceImpl implements SeckillVoucherService {

    @Resource
    private SeckillVoucherMapper voucherMapper;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private SeckillOrderMapper orderMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private DefaultRedisScript<Long> redisScript;

    @Resource
    private RedisLock redisLock;


    @Override
    public void addSeckillVouchers(SeckillVouchers seckillVouchers) {
        // 非空校验
        AssertUtil.isTrue(seckillVouchers.getFkVoucherId()==null,"请选择需要抢购的代金卷！");
        AssertUtil.isTrue(seckillVouchers.getAmount()==0,"请输入抢购总数量");
        Date now = new Date();
        AssertUtil.isNotNull(seckillVouchers.getStartTime(),"请输入开始时间");
        AssertUtil.isNotNull(seckillVouchers.getEndTime(),"请输入结束时间");
        // 过滤时间
        AssertUtil.isTrue(now.after(seckillVouchers.getEndTime()),"结束时间不能遭遇当前时间");
        AssertUtil.isTrue(seckillVouchers.getStartTime().after(seckillVouchers.getEndTime()),"开始时间不能晚于结束时间");

//        -------------- DB业务流程----------------
//        验证数据库中是否已经存在该代金卷的活动了
//        SeckillVouchers vouchers = voucherMapper.selectVoucher(seckillVouchers.getFkVoucherId());
//        AssertUtil.isTrue(vouchers!=null,"该卷已经参与了抢购活动");
//        持久化
//        voucherMapper.save(seckillVouchers);
        //-------------- Redis业务流程----------------
        // 构建key
        String redisKey = RedisKeyConstant.seckill_vouchers.getKey()+seckillVouchers.getFkVoucherId();
        // 查询redis中是否存在活动
//        Object o = redisTemplate.opsForHash().get();
        Map<String,Object> seckillVoucherMaps  = redisTemplate.opsForHash().entries(redisKey);
//        String+hash    大key   idkey---》jsonString
        AssertUtil.isTrue(!seckillVoucherMaps.isEmpty()&&(int)seckillVoucherMaps.get("amount")>0,"该卷已经参与了抢购活动！");
        // 添加活动到redis中
        seckillVouchers.setCreateDate(now);
        seckillVouchers.setUpdateDate(now);
        seckillVouchers.setIsValid(1);
        redisTemplate.opsForHash().putAll(
                redisKey,
                BeanUtil.beanToMap(seckillVouchers)
        );
    }

    /**
     * 减库存
     * @param voucherId  代金卷id
     */
    @Override
    @Transactional
    public ResultInfo stockDec(String access_token,Integer voucherId,String path) {
        // 基本参数校验
        AssertUtil.isTrue(voucherId == null || voucherId < 0, "请选择需要抢购的代金券");
        AssertUtil.isNotEmpty(access_token, "请登录");

        // ----------注释原始的走 关系型数据库 的流程----------
        // 判断此代金券是否加入抢购
        // SeckillVouchers seckillVouchers = seckillVouchersMapper.selectVoucher(voucherId);
        // AssertUtil.isTrue(seckillVouchers == null, "该代金券并未有抢购活动");

        // ----------采用 Redis 解问题----------
        String redisKey = RedisKeyConstant.seckill_vouchers.getKey() + voucherId;
        Map<String, Object> seckillVoucherMaps = redisTemplate.opsForHash().entries(redisKey);
        SeckillVouchers seckillVouchers = BeanUtil.mapToBean(seckillVoucherMaps, SeckillVouchers.class, true, null);

        // 判断是否有效
        AssertUtil.isTrue(seckillVouchers.getIsValid() == 0, "该活动已结束");
        // 判断是否开始、结束
        Date now = new Date();
        AssertUtil.isTrue(now.before(seckillVouchers.getStartTime()), "该抢购还未开始");
        AssertUtil.isTrue(now.after(seckillVouchers.getEndTime()), "该抢购已结束");

        // 判断是否卖完通过 Lua 脚本扣库存时判断
        //AssertUtil.isTrue(seckillVouchers.getAmount() < 1, "该券已经卖完了");

        // 获取登录用户信息
        String url = "http://food-oauth2-server/user/me?access_token={accessToken}";
        ResultInfo resultInfo = restTemplate.getForObject(url, ResultInfo.class, access_token);
        if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            resultInfo.setPath(path);
            return resultInfo;
        }
        // 这里的data是一个LinkedHashMap，SignInDinerInfo
        SignInDinerInfo dinerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) resultInfo.getData(),
                new SignInDinerInfo(), false);
        // 判断登录用户是否已抢到(一个用户针对这次活动只能买一次)
        VoucherOrders order = orderMapper.findDinerOrder(dinerInfo.getId(),
                seckillVouchers.getFkVoucherId());
        AssertUtil.isTrue(order != null, "该用户已抢到该代金券，无需再抢");

        // ----------注释原始的走 关系型数据库 的流程----------
        // 扣库存
        // int count = seckillVouchersMapper.stockDecrease(seckillVouchers.getId());
        // AssertUtil.isTrue(count == 0, "该券已经卖完了");

        // 使用 Redis 锁一个账号只能购买一次
        String lockName = RedisKeyConstant.seckill_lock.getKey()+ ":" + dinerInfo.getId() + ":" + voucherId;
        // 加锁
        long expireTime = seckillVouchers.getEndTime().getTime() - now.getTime();
        String lockKey = redisLock.tryLock(lockName, expireTime);

        try {
            // 不为空意味着拿到锁了，执行下单
            if (StrUtil.isNotBlank(lockKey)) {
                // 下单
                VoucherOrders voucherOrders = new VoucherOrders();
                voucherOrders.setFkDinerId(dinerInfo.getId());
                // Redis 中不需要维护外键信息
                //voucherOrders.setFkSeckillId(seckillVouchers.getId());
                voucherOrders.setFkVoucherId(seckillVouchers.getFkVoucherId());
                String orderNo = IdUtil.getSnowflake(1, 1).nextIdStr();
                voucherOrders.setOrderNo(orderNo);
                voucherOrders.setOrderType(1);
                voucherOrders.setStatus(0);
                long count = orderMapper.save(voucherOrders);
                AssertUtil.isTrue(count == 0, "用户抢购失败");

                // ----------采用 Redis 解问题----------
                // 扣库存
                // long count = redisTemplate.opsForHash().increment(redisKey, "amount", -1);
                // AssertUtil.isTrue(count < 0, "该券已经卖完了");

                // ----------采用 Redis + Lua 解问题----------
                // 扣库存
                List<String> keys = new ArrayList<>();
                keys.add(redisKey);
                keys.add("amount");
                Long amount = (Long) redisTemplate.execute(redisScript, keys);
                AssertUtil.isTrue(amount == null || amount < 1, "该券已经卖完了");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 手动回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            // 解锁
            redisLock.unLock(lockName, lockKey);
            if (e instanceof ParameterException) {
                return ResultUtil.buildError(0, "该券已经卖完了", path);
            }
        }
        return ResultUtil.buildSuccess(path, "抢购成功");
    }
}
