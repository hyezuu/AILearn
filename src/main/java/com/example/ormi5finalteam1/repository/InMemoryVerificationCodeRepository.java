package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.verificationcode.VerificationCode;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryVerificationCodeRepository implements VerificationCodeRepository {

    private final Map<String, VerificationCode> repository = new ConcurrentHashMap<>();

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        repository.put(verificationCode.getCode(), verificationCode);
        return verificationCode;
    }

    @Override
    public Optional<VerificationCode> findByCode(String code) {
        return Optional.ofNullable(repository.get(code));
    }

    @Override
    public void remove(String code) {
        repository.remove(code);
    }
}