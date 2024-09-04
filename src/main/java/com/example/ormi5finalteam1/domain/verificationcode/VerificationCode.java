package com.example.ormi5finalteam1.domain.verificationcode;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class VerificationCode {

    private final String code;
    private final String email;
    private final LocalDateTime createAt;
    private final long expirationTimeInMinutes;

    public VerificationCode(String code, String email, long expirationTimeInMinutes) {
        this.code = code;
        this.email = email;
        this.createAt = LocalDateTime.now();
        this.expirationTimeInMinutes = expirationTimeInMinutes;
    }

    public boolean isExpired(LocalDateTime verifiedAt) {
        LocalDateTime expiredAt = createAt.plusMinutes(expirationTimeInMinutes);
        return verifiedAt.isAfter(expiredAt);
    }
}