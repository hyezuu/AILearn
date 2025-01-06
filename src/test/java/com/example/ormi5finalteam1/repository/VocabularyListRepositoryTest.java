package com.example.ormi5finalteam1.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class VocabularyListRepositoryTest {

    @Autowired
    private VocabularyListRepository vocabularyListRepository;

    @Autowired UserRepository userRepository;

    private long userId;

    @BeforeEach
    void setUp() {
        User user = User.builder()
            .email("test@test.com")
            .password("password")
            .nickname("test")
            .build();
        User savedUser = userRepository.save(user);
        userId = savedUser.getId();

        VocabularyList vocabularyList = new VocabularyList(savedUser);
        vocabularyListRepository.save(vocabularyList);
    }

    @Test
    void findByUserId_는_유저_id_로_단어장을_찾아올_수_있다() {
        //given
        assertThat(userId).isNotNull();
        //when
        Optional<VocabularyList> result = vocabularyListRepository.findByUserId(userId);
        //then
        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(userId);
    }


}