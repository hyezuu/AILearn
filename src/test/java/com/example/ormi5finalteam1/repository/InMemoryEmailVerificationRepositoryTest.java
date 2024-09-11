package com.example.ormi5finalteam1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class InMemoryEmailVerificationRepositoryTest {

    @Autowired
    private InMemoryEmailVerificationRepository repository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        repository.save("test@example.com");
        repository.save("another@example.com");
    }

    @Test
    void save_는_이메일을_저장하고_true를_반환한다() {
        // given
        String email = "new@example.com";
        // when
        repository.save(email);
        // then
        assertThat(repository.existByEmail(email)).isTrue();
    }

    @Test
    void existByEmail_은_존재하는_이메일에_대해_true를_반환한다() {
        // when
        boolean result = repository.existByEmail("test@example.com");
        // then
        assertThat(result).isTrue();
    }

    @Test
    void existByEmail_은_존재하지_않는_이메일에_대해_false를_반환한다() {
        // when
        boolean result = repository.existByEmail("nonexistent@example.com");
        // then
        assertThat(result).isFalse();
    }

    @Test
    void remove_는_지정된_이메일을_제거한다() {
        // when
        repository.remove("test@example.com");
        // then
        assertThat(repository.existByEmail("test@example.com")).isFalse();
    }

    @Test
    void save_기존_이메일_업데이트() {
        // given
        String email = "update@example.com";
        repository.save(email);
        // when
        repository.save(email); // 다시 저장
        // then
        assertThat(repository.existByEmail(email)).isTrue();
    }
}