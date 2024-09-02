package com.example.ormi5finalteam1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyListVocabulary;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class VocabularyListVocabularyRepositoryTest {

    @Autowired
    private VocabularyListVocabularyRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private VocabularyList vocabularyList;

    @BeforeEach
    void setUp() {
        User user = User.builder()
            .email("test@test.com")
            .password("password")
            .nickname("test")
            .build();
        entityManager.persist(user);

        vocabularyList = new VocabularyList(user);
        entityManager.persist(vocabularyList);

        Vocabulary vocabulary1 = new Vocabulary("word1", "meaning1", Grade.A1, "example1");
        Vocabulary vocabulary2 = new Vocabulary("word2", "meaning2", Grade.A1, "example2");
        Vocabulary vocabulary3 = new Vocabulary("word3", "meaning3", Grade.A2, "example3");
        entityManager.persist(vocabulary1);
        entityManager.persist(vocabulary2);
        entityManager.persist(vocabulary3);

        entityManager.persist(new VocabularyListVocabulary(vocabularyList, vocabulary1, Grade.A1));
        entityManager.persist(new VocabularyListVocabulary(vocabularyList, vocabulary2, Grade.A1));
        entityManager.persist(new VocabularyListVocabulary(vocabularyList, vocabulary3, Grade.A2));

        entityManager.flush();
    }

    @Test
    void findMaxVocabularyIdByVocabularyListIdAndGrade_로_최대_단어_id를_찾아올_수_있다() {
        //given
        //when
        Optional<Long> result = repository
            .findMaxVocabularyIdByVocabularyListIdAndGrade(vocabularyList.getId(), Grade.A1);
        //then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(2L);
    }

    @Test
    void findMaxVocabularyIdByVocabularyListIdAndGrade_은_해당등급의_단어가_없으면_Optional_Empty_를_내려준다() {
        //given
        //when
        Optional<Long> result = repository
            .findMaxVocabularyIdByVocabularyListIdAndGrade(vocabularyList.getId(), Grade.B1);
        //then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void findMaxVocabularyIdByVocabularyListIdAndGrade_는_존재하지_않는_단어장ID로_조회하면_Optional_Empty_를_내려준다() {
        // given
        Long nonExistentListId = 9999L;
        // when
        Optional<Long> result = repository
            .findMaxVocabularyIdByVocabularyListIdAndGrade(nonExistentListId, Grade.A1);
        // then
        assertThat(result.isEmpty()).isTrue();
    }
}