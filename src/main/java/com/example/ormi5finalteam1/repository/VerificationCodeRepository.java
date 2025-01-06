package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.verificationcode.VerificationCode;
import java.util.Optional;

public interface VerificationCodeRepository {

    void save(VerificationCode verificationCode);

    Optional<String> findByEmail(String email);

    void remove(String email);
}