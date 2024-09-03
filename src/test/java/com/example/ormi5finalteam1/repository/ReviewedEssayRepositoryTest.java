package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.essay.Essay;
import com.example.ormi5finalteam1.domain.essay.ReviewedEssays;
import com.example.ormi5finalteam1.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ReviewedEssayRepositoryTest {

    @Autowired
    private ReviewedEssaysRepository reviewedEssaysRepository;

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private UserRepository userRepository;

    private ReviewedEssays reviewedEssays;
    private Essay savedEssay;
    private User savedUser;

    @BeforeEach
    void setUp() {
        // 초기 데이터를 설정
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

        reviewedEssays = ReviewedEssays.builder()
                .content("Sample feedback content")
                .essay(essay)
                .build();
        reviewedEssaysRepository.save(reviewedEssays); // 초기 데이터 저장
    }


    @Test
    void existsByEssayId_리턴_TRUE_정상_데이터_있을_때() {
        // when
        boolean exists = reviewedEssaysRepository.existsByEssayId(savedEssay.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEssayId_리턴_FALSE_데이터_없을_때() {
        // when
        boolean exists = reviewedEssaysRepository.existsByEssayId(99L); // 저장되지 않은 ID

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void findByEssayId_정상적으로_조회() {
        // when
        ReviewedEssays foundReview = reviewedEssaysRepository.findByEssayId(savedEssay.getId());

        // then
        assertThat(foundReview).isNotNull();
        assertThat(foundReview.getContent()).isEqualTo("Sample feedback content");
    }

    @Test
    void findByEssayId_데이터_없을_때_NULL_리턴() {
        // when
        ReviewedEssays foundReview = reviewedEssaysRepository.findByEssayId(99L); // 저장되지 않은 ID

        // then
        assertThat(foundReview).isNull();
    }
}
