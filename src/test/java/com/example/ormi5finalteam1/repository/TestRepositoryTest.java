package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class TestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TestRepository testRepository;

    @BeforeEach
    public void setUp() {
        Test newTest1 = new Test(Grade.A2, "Can you _______ me your pen, please? (lend, borrow, give, take)", "lend");
        Test newTest2 = new Test(Grade.A2, "She’s learning to play the _______. (guitar, car, tree, shoe)", "guitar");
        Test newTest3 = new Test(Grade.A1, "I usually _______ up at 7 o’clock in the morning. (stand, sit, wake, sleep)", "wake");
        Test newTest4 = new Test(Grade.B2, "She _______ her success to hard work and determination. (attributed, contributed, distributed, attributed)", "attributed");
        Test newTest5 = new Test(Grade.B1, "The weather forecast says it will _______ tomorrow. (rain, raining, rained, rains)", "rain");
        Test newTest6 = new Test(Grade.A2, "What _______ of music do you like? (type, color, size, weather)", "type");

        entityManager.persist(newTest1);
        entityManager.persist(newTest2);
        entityManager.persist(newTest3);
        entityManager.persist(newTest4);
        entityManager.persist(newTest5);
        entityManager.persist(newTest6);
        entityManager.flush();
    }
    @org.junit.jupiter.api.Test
    @DisplayName("findByGrade 성공 테스트")
    public void testFindByGradeSuccess() {

        List<Test> testList = testRepository.findByGrade(Grade.A2);

        assertThat(testList).isNotEmpty();
        assertThat(testList.size()).isEqualTo(3);
        for (Test test : testList) {
            assertThat(test.getGrade()).isEqualTo(Grade.A2);
        }
    }

    @org.junit.jupiter.api.Test
    @DisplayName("findByGrade 실패 테스트")
    public void testFindByGradeFail() {

        List<Test> testList = testRepository.findByGrade(Grade.C2);

        assertThat(testList).isEmpty();
    }

}
