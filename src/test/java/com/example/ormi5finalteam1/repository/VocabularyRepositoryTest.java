package com.example.ormi5finalteam1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class VocabularyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @BeforeEach
    void setUp() {
        entityManager.persist(new Vocabulary("apple", "사과", Grade.A1, "I eat an apple every day."));
        entityManager.persist(
            new Vocabulary("banana", "바나나", Grade.A1, "I like banana smoothies."));
        entityManager.persist(
            new Vocabulary("computer", "컴퓨터", Grade.A2, "I use a computer for work."));
        for (int i = 0; i < 15; i++) {
            entityManager.persist(new Vocabulary("word" + i, "의미" + i, Grade.B1, "예문" + i));
        }
        entityManager.flush();
    }

    @Test
    void existsByWord_는_단어가_존재하는_경우_true_를_반환한다() {
        //given
        //when
        boolean result = vocabularyRepository.existsByWord("apple");
        //then
        assertThat(result).isTrue();
    }

    @Test
    void existsByWord_는_단어가_존재하지_않는_경우_false_를_반환한다() {
        //given
        //when
        boolean result = vocabularyRepository.existsByWord("nonexistent");
        //then
        assertThat(result).isFalse();
    }

    @Test
    void findTop10ByGradeAndIdGreaterThanOrderById_는_주어진_조건에_맞는_단어를_10개까지_반환한다() {
        //given
        //when
        List<Vocabulary> result = vocabularyRepository.findTop10ByGradeAndIdGreaterThanOrderById(
            Grade.B1, 0L);
        //then
        assertThat(result).hasSize(10);
        assertThat(result).allMatch(vocabulary -> vocabulary.getGrade() == Grade.B1);
        assertThat(result).isSortedAccordingTo(Comparator.comparing(Vocabulary::getId));
    }

    @Test
    void findTop10ByGradeAndIdGreaterThanOrderById_는_결과가_10개_미만인_경우_모든_결과를_반환한다() {
        //given
        //when
        List<Vocabulary> result = vocabularyRepository.findTop10ByGradeAndIdGreaterThanOrderById(
            Grade.A1, 0L);
        //then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(vocabulary -> vocabulary.getGrade() == Grade.A1);
    }

    @Test
    void findTop10ByGradeAndIdGreaterThanOrderById_는_주어진_Id보다_큰_단어만_반환한다() {
        //given
        Vocabulary lastB1Word = vocabularyRepository.findTop10ByGradeAndIdGreaterThanOrderById(
            Grade.B1, 0L).get(0);
        //when
        List<Vocabulary> result = vocabularyRepository.findTop10ByGradeAndIdGreaterThanOrderById(
            Grade.B1, lastB1Word.getId());
        //then
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(vocabulary -> vocabulary.getId() > lastB1Word.getId());
    }
}