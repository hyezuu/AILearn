package com.example.ormi5finalteam1.domain.verificationcode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VerificationCodeTest {

    @Test
    void 생성_시_올바른_값으로_초기화된다() {
        // given
        String code = "123456";
        String email = "test@example.com";
        long expirationTimeInMinutes = 5;

        // when
        VerificationCode verificationCode = new VerificationCode(code, email, expirationTimeInMinutes);

        // then
        assertThat(verificationCode.getCode()).isEqualTo(code);
        assertThat(verificationCode.getEmail()).isEqualTo(email);
        assertThat(verificationCode.getExpirationTimeInMinutes()).isEqualTo(expirationTimeInMinutes);
        assertThat(verificationCode.getCreateAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void 만료_전에는_isExpired가_false를_반환한다() {
        // given
        VerificationCode verificationCode = new VerificationCode("123456", "test@example.com", 5);
        LocalDateTime beforeExpiration = LocalDateTime.now().plusMinutes(4);
        // when
        boolean isExpired = verificationCode.isExpired(beforeExpiration);
        // then
        assertThat(isExpired).isFalse();
    }

    @Test
    void 만료_후에는_isExpired가_true를_반환한다() {
        // given
        VerificationCode verificationCode = new VerificationCode("123456", "test@example.com", 5);
        LocalDateTime afterExpiration = LocalDateTime.now().plusMinutes(6);
        // when
        boolean isExpired = verificationCode.isExpired(afterExpiration);
        // then
        assertThat(isExpired).isTrue();
    }

    @Test
    void 정확히_만료_시간에는_isExpired가_false를_반환한다() {
        // given
        VerificationCode verificationCode = new VerificationCode("123456", "test@example.com", 5);
        LocalDateTime exactExpirationTime = verificationCode.getCreateAt().plusMinutes(5);
        // when
        boolean isExpired = verificationCode.isExpired(exactExpirationTime);
        // then
        assertThat(isExpired).isFalse();
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -100})
    void 유효하지_않은_만료_시간으로_생성하면_예외가_발생한다(long invalidExpirationTime) {
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new VerificationCode("123456", "test@example.com", invalidExpirationTime));
    }
}