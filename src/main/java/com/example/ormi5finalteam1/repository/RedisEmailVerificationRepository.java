package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
@RequiredArgsConstructor
public class RedisEmailVerificationRepository implements EmailVerificationRepository {

    private static final String KEY_PREFIX = "email_verification:";
    private static final long EXPIRE_MINUTES = 60;
    private final RedisUtil redisUtil;

    @Override
    public void save(String email) {
        redisUtil.setBooleanData(KEY_PREFIX + email, true, EXPIRE_MINUTES);
    }

    @Override
    public boolean existByEmail(String email) {
        return redisUtil.getBooleanData(KEY_PREFIX + email);
    }

    @Override
    public void remove(String email) {
        redisUtil.deleteData(KEY_PREFIX + email);
    }
}
