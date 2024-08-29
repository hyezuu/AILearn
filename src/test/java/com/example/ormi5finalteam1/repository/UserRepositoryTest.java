package com.example.ormi5finalteam1.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.ormi5finalteam1.domain.user.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
            .email("test@test.com")
            .password("password")
            .nickname("test")
            .build();
        userRepository.save(user);
    }

    @Test
    void findByEmail_로_유저_데이터를_찾아올_수_있다() {
        //given
        //when
        Optional<User> result = userRepository.findByEmail("test@test.com");
        //then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void findByEmail_은_데이터가_없으면_Optional_Empty_를_내려준다() {
        //given
        //when
        Optional<User> result = userRepository.findByEmail("");
        //then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void existByEmail_은_해당_이메일이_있으면_true_를_반환한다() {
        //given
        //when
        Boolean result = userRepository.existsByEmail("test@test.com");
        //then
        assertThat(result).isTrue();
    }

    @Test
    void existByEmail_은_해당_이메일이_없으면_false_를_반환한다() {
        //given
        //when
        Boolean result = userRepository.existsByEmail("none@test.com");
        //then
        assertThat(result).isFalse();
    }


}