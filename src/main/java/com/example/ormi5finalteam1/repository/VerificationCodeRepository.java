package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.verificationcode.VerificationCode;
import java.util.Optional;

public interface VerificationCodeRepository {

    VerificationCode save(VerificationCode verificationCode);

    Optional<VerificationCode> findByCode(String code);

    void remove(String code);
}