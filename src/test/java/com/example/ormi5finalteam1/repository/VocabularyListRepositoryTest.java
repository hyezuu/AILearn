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

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        User user = User.builder()
            .email("test@test.com")
            .password("password")
            .nickname("test")
            .build();
        entityManager.persist(user);

        VocabularyList vocabularyList = new VocabularyList(user);
        entityManager.persist(vocabularyList);

        entityManager.flush();
    }

    @Test
    void findByUserId_는_유저_id_로_단어장을_찾아올_수_있다() {
        //given
        //when
        Optional<VocabularyList> result = vocabularyListRepository.findByUserId(1L);
        //then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getUser().getId()).isEqualTo(1L);
    }
}