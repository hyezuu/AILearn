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
    public void save(VerificationCode verificationCode) {
        repository.put(verificationCode.getEmail(), verificationCode);
    }

    @Override
    public Optional<String> findByEmail(String email) {
        return Optional.ofNullable(repository.get(email))
            .map(VerificationCode::getCode);
    }

    @Override
    public void remove(String email) {
        repository.remove(email);
    }
}