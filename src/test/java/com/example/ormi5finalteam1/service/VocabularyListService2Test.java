package com.example.ormi5finalteam1.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyListVocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.dto.MyVocabularyListResponseDto;
import com.example.ormi5finalteam1.repository.UserRepository;
import com.example.ormi5finalteam1.repository.VocabularyListRepository;
import com.example.ormi5finalteam1.repository.VocabularyListVocabularyRepository;
import com.example.ormi5finalteam1.repository.VocabularyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VocabularyListService2Test {

    @Autowired
    private VocabularyListService vocabularyListService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private VocabularyListRepository vocabularyListRepository;

    @Autowired
    private VocabularyListVocabularyRepository vocabularyListVocabularyRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private VocabularyList testVocabularyList;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        testUser = userRepository.save(User.builder()
            .email("test@test.com")
            .password("password")
            .nickname("test")
            .build());

        testVocabularyList = vocabularyListRepository.save(new VocabularyList(testUser));

        // 1000개의 테스트 데이터 생성
        for (int i = 1; i <= 1000; i++) {
            Vocabulary vocabulary = vocabularyRepository.save(
                new Vocabulary("word" + i, "meaning" + i, Grade.A1, "example" + i));

            vocabularyListVocabularyRepository.save(
                new VocabularyListVocabulary(testVocabularyList, vocabulary, Grade.A1));
        }
    }

    @Test
    @DisplayName("세 가지 조회 방식의 성능 비교")
    void comparePerformance() throws Exception {
        // Given
        int iterations = 10;
        PageRequest pageRequest = PageRequest.of(0, 20);
        MetricsResult basicMetrics = new MetricsResult();
        MetricsResult fetchMetrics = new MetricsResult();
        MetricsResult dtoMetrics = new MetricsResult();

        // When & Then
        // 1. Basic Method
        System.out.println("basic 메서드 테스트 시작");
        for (int i = 0; i < iterations; i++) {
            resetQueryCount();
            long start = System.nanoTime();
            Page<MyVocabularyListResponseDto> basicResult =
                vocabularyListService.getMyVocabulariesBasic(testUser.getId(), pageRequest);
            long end = System.nanoTime();

            basicMetrics.addTime(end - start);
            basicMetrics.addQueryCount(getQueryCount());

            assertThat(basicResult).isNotNull();
            assertThat(basicResult.getContent()).hasSize(20);
        }

        System.out.println("join fetch 메서드 테스트 시작");
        // 2. Join Fetch
        for (int i = 0; i < iterations; i++) {
            resetQueryCount();
            long start = System.nanoTime();
            Page<MyVocabularyListResponseDto> fetchResult =
                vocabularyListService.getMyVocabulariesJoinFetch(testUser.getId(), pageRequest);
            long end = System.nanoTime();

            fetchMetrics.addTime(end - start);
            fetchMetrics.addQueryCount(getQueryCount());

            assertThat(fetchResult).isNotNull();
            assertThat(fetchResult.getContent()).hasSize(20);
        }

        System.out.println("DTO Projection 메서드 테스트 시작");
        // 3. DTO Projection
        for (int i = 0; i < iterations; i++) {
            resetQueryCount();
            long start = System.nanoTime();
            Page<MyVocabularyListResponseDto> dtoResult =
                vocabularyListService.getMyVocabulariesDtoProjection(testUser.getId(), pageRequest);
            long end = System.nanoTime();

            dtoMetrics.addTime(end - start);
            dtoMetrics.addQueryCount(getQueryCount());

            assertThat(dtoResult).isNotNull();
            assertThat(dtoResult.getContent()).hasSize(20);
        }

        // 결과 출력
        System.out.println("\n=== 성능 비교 결과 ===");
        System.out.printf("Basic Method   - 평균 실행 시간: %.2f ms, 평균 쿼리 수: %.1f%n",
            basicMetrics.getAverageTimeMs(), basicMetrics.getAverageQueryCount());
        System.out.printf("Join Fetch     - 평균 실행 시간: %.2f ms, 평균 쿼리 수: %.1f%n",
            fetchMetrics.getAverageTimeMs(), fetchMetrics.getAverageQueryCount());
        System.out.printf("DTO Projection - 평균 실행 시간: %.2f ms, 평균 쿼리 수: %.1f%n",
            dtoMetrics.getAverageTimeMs(), dtoMetrics.getAverageQueryCount());
    }

    // 메트릭스 결과를 저장하기 위한 내부 클래스
    private static class MetricsResult {
        private long totalTime = 0;
        private long totalQueries = 0;
        private int count = 0;

        public void addTime(long time) {
            totalTime += time;
            count++;
        }

        public void addQueryCount(long queries) {
            totalQueries += queries;
        }

        public double getAverageTimeMs() {
            return totalTime / (count * 1_000_000.0);
        }

        public double getAverageQueryCount() {
            return totalQueries / (double) count;
        }
    }
    private void resetQueryCount() {
        SessionFactory sessionFactory = entityManager
            .getEntityManagerFactory()
            .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().clear();
    }

    private long getQueryCount() {
        SessionFactory sessionFactory = entityManager
            .getEntityManagerFactory()
            .unwrap(SessionFactory.class);
        return sessionFactory.getStatistics().getQueryExecutionCount();
    }
}
