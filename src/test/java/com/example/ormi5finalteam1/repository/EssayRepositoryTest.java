package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.essay.Essay;
import com.example.ormi5finalteam1.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class EssayRepositoryTest {

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;
    private Essay savedEssay;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("test")
                .build();
        savedUser = userRepository.save(user);

        Essay essay = Essay.builder()
                .user(savedUser)
                .topic("topicTest")
                .content("contentTest")
                .build();
        savedEssay = essayRepository.save(essay);
    }

    @Test
    void findById_로_에세이_데이터를_찾아올_수_있다() {
        //given
        //when
        Optional<Essay> result = essayRepository.findById(savedEssay.getId());
        //then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getContent()).isEqualTo("contentTest");
    }

    @Test
    void findById_는_데이터가_없으면_Optional_Empty_를_내려준다() {
        //given
        //when
        Optional<Essay> result = essayRepository.findById(3L);
        //then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void findByUserId_로_에세이_데이터를_찾아올_수_있다() {
        //given
        Pageable pageable = PageRequest.of(0,3);
        //when
        Page<Essay> result = essayRepository.findByUserId(savedUser.getId(),pageable);
        //then
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.getContent().get(0).getContent()).isEqualTo("contentTest");
    }

    @Test
    void findByUserId_는_에세이가_없으면_빈_페이지를_반환한다() {
        //given
        Pageable pageable = PageRequest.of(0, 3);
        //when
        Page<Essay> result = essayRepository.findByUserId(999L, pageable); // 존재하지 않는 사용자 ID
        //then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void findByUserId_는_여러_에세이를_가져올_수_있다() {
        //given
        Pageable pageable = PageRequest.of(0, 3);

        // 추가로 두 개의 Essay를 더 저장.
        Essay essay1 = Essay.builder()
                .user(savedUser)
                .topic("topicTest1")
                .content("contentTest1")
                .build();
        Essay essay2 = Essay.builder()
                .user(savedUser)
                .topic("topicTest2")
                .content("contentTest2")
                .build();
        essayRepository.save(essay1);
        essayRepository.save(essay2);

        //when
        Page<Essay> result = essayRepository.findByUserId(savedUser.getId(), pageable);

        //then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("contentTest");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("contentTest1");
        assertThat(result.getContent().get(2).getContent()).isEqualTo("contentTest2");
    }


}
