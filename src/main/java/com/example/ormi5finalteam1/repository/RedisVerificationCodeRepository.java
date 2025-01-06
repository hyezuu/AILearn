package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.common.util.RedisUtil;
import com.example.ormi5finalteam1.domain.verificationcode.VerificationCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
@RequiredArgsConstructor
public class RedisVerificationCodeRepository implements VerificationCodeRepository {

    private static final String KEY_PREFIX = "verificationCode:";
    private final RedisUtil redisUtil;

    @Override
    public void save(VerificationCode verificationCode) {
        redisUtil.setDataExpire(KEY_PREFIX + verificationCode.getEmail(), verificationCode.getCode(),
            verificationCode.getExpirationTimeInMinutes());
    }

    @Override
    public Optional<String> findByEmail(String email) {
        return Optional.ofNullable(redisUtil.getData(KEY_PREFIX + email));
    }

    @Override
    public void remove(String email) {
        redisUtil.deleteData(KEY_PREFIX+email);
    }
}
