package com.example.ormi5finalteam1.entity;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.Test;
import com.example.ormi5finalteam1.domain.test.TestQuestionResponseDto;
import com.example.ormi5finalteam1.repository.TestRepository;
import com.example.ormi5finalteam1.service.TestService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestTest {

    @Mock
    private TestRepository testRepository;

    @InjectMocks
    private TestService testService;

    @org.junit.jupiter.api.Test
    public void testQuestionCreation(){
        Test question = new Test(1L, Grade.A2, "사과는 영어로?", "apple");

        assertThat(question.getQuestion()).isEqualTo("사과는 영어로?");
        assertThat(question.getGrade()).isEqualTo(Grade.A2);
        assertThat(question.getAnswer()).isEqualTo("apple");
    }

    @org.junit.jupiter.api.Test
    public void testQuestionRead() {
        List<Test> questions= new ArrayList<>();
        questions.add(new Test(1L, Grade.A2, "파인애플은 영어로?", "pineapple"));
        questions.add(new Test(2L, Grade.A2, "바나나는 영어로?", "banana"));
        when(testRepository.findByGrade(Grade.A2)).thenReturn(questions);

        List<TestQuestionResponseDto> results = testService.getTests(Grade.A1, Grade.A2);
        assertThat(results.get(0).getQuestion()).isEqualTo("파인애플은 영어로?");
        assertThat(results.get(0).getGrade()).isEqualTo(Grade.A2);
    }

}
