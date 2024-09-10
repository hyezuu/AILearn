package com.example.ormi5finalteam1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ormi5finalteam1.domain.verificationcode.VerificationCode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class InMemoryVerificationCodeRepositoryTest {

    @Autowired
    private InMemoryVerificationCodeRepository verificationCodeRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        verificationCodeRepository.save(new VerificationCode("123456", "test@example.com", 5));
        verificationCodeRepository.save(new VerificationCode("654321", "another@example.com", 5));
    }

    @Test
    void save_는_새로운_인증코드를_저장한다() {
        // given
        VerificationCode newCode = new VerificationCode("111111", "new@example.com", 5);
        // when
        VerificationCode savedCode = verificationCodeRepository.save(newCode);
        // then
        assertThat(savedCode).isEqualTo(newCode);
        assertThat(verificationCodeRepository.findByEmail("new@example.com")).isPresent();
    }

    @Test
    void findByEmail_은_존재하는_이메일에_대해_인증코드를_반환한다() {
        // when
        Optional<VerificationCode> result = verificationCodeRepository.findByEmail(
            "test@example.com");
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("123456");
    }

    @Test
    void findByEmail_은_존재하지_않는_이메일에_대해_빈_Optional을_반환한다() {
        // when
        Optional<VerificationCode> result = verificationCodeRepository.findByEmail(
            "nonexistent@example.com");
        // then
        assertThat(result).isEmpty();
    }

    @Test
    void remove_는_지정된_이메일의_인증코드를_제거한다() {
        // when
        verificationCodeRepository.remove("test@example.com");
        // then
        assertThat(verificationCodeRepository.findByEmail("test@example.com")).isEmpty();
    }

    @Test
    void concurrentOperations_유지일관성() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount/5);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final String email = "test" + i + "@example.com";
            final String code = String.format("%06d", i);
            executorService.submit(() -> {
                try {
                    verificationCodeRepository.save(new VerificationCode(code, email, 5));
                    assertThat(verificationCodeRepository.findByEmail(email)).isPresent();
                    verificationCodeRepository.remove(email);
                    assertThat(verificationCodeRepository.findByEmail(email)).isEmpty();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // 모든 작업이 완료된 후 저장소가 비어있는지 확인
        for (int i = 0; i < threadCount; i++) {
            assertThat(
                verificationCodeRepository.findByEmail("test" + i + "@example.com")).isEmpty();
        }
    }

    @Test
    void save_기존_코드_업데이트() {
        // given
        String email = "update@example.com";
        VerificationCode initialCode = new VerificationCode("123456", email, 5);
        verificationCodeRepository.save(initialCode);
        // when
        VerificationCode updatedCode = new VerificationCode("654321", email, 10);
        verificationCodeRepository.save(updatedCode);
        // then
        Optional<VerificationCode> result = verificationCodeRepository.findByEmail(email);
        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("654321");
        assertThat(result.get().getExpirationTimeInMinutes()).isEqualTo(10);
    }
}