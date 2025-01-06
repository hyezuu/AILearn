package com.example.ormi5finalteam1.common.util;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    public String getData(String key) { // key를 통해 value(데이터)를 얻는다.
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setDataExpire(String key, String value, long duration) {
        //  duration 동안 (key, value)를 저장한다.
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration * 60 * 1000);
        valueOperations.set(key, value, expireDuration);
    }

    public void setBooleanData(String key, boolean value, long duration) {
        // boolean 값을 문자열로 변환해 저장
        String booleanValue = Boolean.toString(value);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration * 60 * 1000);
        valueOperations.set(key, booleanValue, expireDuration);
    }

    public boolean getBooleanData(String key) {
        // 문자열로 저장된 값을 boolean으로 변환
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String data = valueOperations.get(key);
        return Boolean.parseBoolean(data);
    }

    public void deleteData(String key) {
        // 데이터 삭제
        redisTemplate.delete(key);
    }
}
