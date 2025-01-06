package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyListVocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.dto.MyVocabularyListResponseDto;
import jakarta.transaction.Transactional;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class VocabularyListVocabularyRepositoryTest {

    @Autowired
    private VocabularyListVocabularyRepository repository;

    @Autowired
    private VocabularyListRepository vocabularyListRepository;

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

        // 1000개의 단어를 생성
        for (int i = 1; i <= 1000; i++) {
            Vocabulary vocabulary = new Vocabulary("word" + i, "meaning" + i, Grade.A1,
                "example" + i);
            entityManager.persist(vocabulary);

            // 1000개의 VocabularyListVocabulary 객체를 생성
            entityManager.persist(
                new VocabularyListVocabulary(vocabularyList, vocabulary, Grade.A1));
        }

        entityManager.flush();
    }
//
//    @Test
//    void findMaxVocabularyIdByVocabularyListIdAndGrade_로_최대_단어_id를_찾아올_수_있다() {
//        //given
//        //when
//        Optional<Long> result = repository
//            .findMaxVocabularyIdByVocabularyListIdAndGrade(vocabularyList.getId(), Grade.A1);
//        //then
//        assertThat(result.isPresent()).isTrue();
//        assertThat(result.get()).isEqualTo(2L);
//    }
//
//    @Test
//    void findMaxVocabularyIdByVocabularyListIdAndGrade_은_해당등급의_단어가_없으면_Optional_Empty_를_내려준다() {
//        //given
//        //when
//        Optional<Long> result = repository
//            .findMaxVocabularyIdByVocabularyListIdAndGrade(vocabularyList.getId(), Grade.B1);
//        //then
//        assertThat(result.isEmpty()).isTrue();
//    }

//    @Test
//    void findMaxVocabularyIdByVocabularyListIdAndGrade_는_존재하지_않는_단어장ID로_조회하면_Optional_Empty_를_
//    내려준다() {
//        // given
//        Long nonExistentListId = 9999L;
//        // when
//        Optional<Long> result = repository
//            .findMaxVocabularyIdByVocabularyListIdAndGrade(nonExistentListId, Grade.A1);
//        // then
//        assertThat(result.isEmpty()).isTrue();
//    }

    @Test
    @Transactional
    public void testBasicMethodPerformance() {
        resetQueryCount();
        long start = System.currentTimeMillis();

        Page<VocabularyListVocabulary> result = repository
            .findByVocabularyListUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(1L,
                PageRequest.of(0, 10));

        StringBuilder output = new StringBuilder();

        // 실제 데이터 접근하여 N+1 발생 유도
        result.getContent().forEach(vlv -> {
           output.append(vlv.getVocabulary().getWord())
               .append(vlv.getVocabularyList().getUser().getNickname());// vocabularyList 접근
        });

        long end = System.currentTimeMillis();
        System.out.println("===== Basic Method Test =====");
        System.out.println(output);
        System.out.println("실행 시간: " + (end - start) + "ms");
        System.out.println("쿼리 수: " + queryCount());
        System.out.println("==========================");
    }

    @Test
    @Transactional
    public void testOptimizedMethodPerformance() {
        resetQueryCount();
        long start = System.currentTimeMillis();
        Page<VocabularyListVocabulary> result = repository
            .findByUserIdOrderByCreatedAtDesc(1L, PageRequest.of(0, 10));

        StringBuilder output = new StringBuilder();

        // 실제 데이터 접근하여 N+1 발생 유도
        result.getContent().forEach(vlv -> {
            output.append(vlv.getVocabulary().getWord())
                .append(vlv.getVocabularyList().getUser().getNickname());// vocabularyList 접근
        });

        long end = System.currentTimeMillis();
        System.out.println("===== Optimized Method Test =====");
        System.out.println(output);
        System.out.println("실행 시간: " + (end - start) + "ms");
        System.out.println("쿼리 수: " + queryCount());
        System.out.println("==========================");
    }

    @Test
    @Transactional
    public void testMoreOptimizedMethodPerformance() {

        resetQueryCount();
        long start = System.currentTimeMillis();

        Page<MyVocabularyListResponseDto> result = repository.findMyVocabularyList(1L,
            PageRequest.of(0, 10));

        long end = System.currentTimeMillis();
        System.out.println("최적화된 메서드 실행 시간: " + (end - start) + "ms");
        System.out.println("쿼리 수: " + queryCount());
    }


    private long queryCount() {
        // Hibernate SessionFactory를 통해 Statistics 객체를 가져옴
        SessionFactoryImpl sessionFactory = entityManager.getEntityManager()
            .getEntityManagerFactory()
            .unwrap(SessionFactoryImpl.class);

        Statistics statistics = sessionFactory.getStatistics();

        // Query execution count 리턴
        return statistics.getQueryExecutionCount();
    }

    private void resetQueryCount() {
        SessionFactoryImpl sessionFactory = entityManager.getEntityManager()
            .getEntityManagerFactory()
            .unwrap(SessionFactoryImpl.class);

        Statistics statistics = sessionFactory.getStatistics();
        statistics.clear();
    }
}