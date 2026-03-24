package com.example_2.haidaodemo_v1.common.Utils;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * 🛰️ Redis工具类
 * <hr/>
 * 🧩 职责：处理缓存防护，提供基础的存取
 * 🛡️ 关联：
 * 🗺️ 架构：common (haidaoDemo_v1)
 *
 * @author Sisfuzues
 * @date 2026/3/11 17:28
 */
@Component
public class RedisUtils {
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisUtils(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     *  通用存入
     * <hr/>
     * 🧩 逻辑： 规范存储一个Java对象进入Redis
     * 🛡️ 依赖：
     * ⚠️ 注意：
     *
     * @param  key value ttl timeUnit  字符串代表插入字段，value是一个Java对象，
     *                                 ttl代表过期时间，最后一个是单位
     * @author Sisfuzues
     * @date 2026/3/11 18:34
     */
    public void set(String key, Object value, long ttl, TimeUnit timeUnit) {
        String toJson = JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(
                key,
                toJson,
                ttl,
                timeUnit);
    }
}
