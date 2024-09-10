package com.example.ormi5finalteam1.service;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.external.api.service.AlanAIService;
import com.example.ormi5finalteam1.external.api.util.ContentParser;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SchedulerServiceTest {

    @InjectMocks
    private SchedulerService schedulerService;

    @Mock
    private AlanAIService alanAIService;
    @Mock
    private VocabularyService vocabularyService;
    @Mock
    private TestService testService;
    @Mock
    private ContentParser contentParser;

    private List<Vocabulary> mockVocabularies;
    private List<com.example.ormi5finalteam1.domain.test.Test> mockTests;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockVocabularies = Arrays.asList(
            new Vocabulary("word1", "meaning1", Grade.A1, "example1"),
            new Vocabulary("word2", "meaning2", Grade.B1, "example2")
        );

        mockTests = Arrays.asList(
            new com.example.ormi5finalteam1.domain.test.Test(Grade.B1, "question1", "answer1"),
            new com.example.ormi5finalteam1.domain.test.Test(Grade.C1, "question2", "answer2")
        );
    }

    @Test
    void getVocabulary_정상적으로_모든_등급에_대해_실행된다() throws IOException {
        // given
        when(alanAIService.getVocabularyResponseForGrade(anyString())).thenReturn("mock content");
        when(contentParser.parseVocabularies(anyString(), any(Grade.class))).thenReturn(
            mockVocabularies);
        when(contentParser.parseTests(anyString(), any(Grade.class))).thenReturn(mockTests);
        // when
        schedulerService.getVocabulary();
        // then
        verify(alanAIService, times(Grade.getGrades().length)).getVocabularyResponseForGrade(
            anyString());
        verify(vocabularyService, times(Grade.getGrades().length)).saveVocabularies(anyList());
        verify(testService, times(Grade.getGrades().length - 1)).saveTests(anyList());  // A1 제외
    }

    @Test
    void getVocabulary_예외_발생시_다른_등급_처리를_계속한다() throws IOException {
        // given
        when(alanAIService.getVocabularyResponseForGrade("A1")).thenThrow(
            new RuntimeException("Test exception"));
        when(alanAIService.getVocabularyResponseForGrade(not(eq("A1")))).thenReturn("mock content");
        when(contentParser.parseVocabularies(anyString(), any(Grade.class))).thenReturn(
            mockVocabularies);
        when(contentParser.parseTests(anyString(), any(Grade.class))).thenReturn(mockTests);
        // when
        schedulerService.getVocabulary();
        // then
        verify(alanAIService, times(Grade.getGrades().length)).getVocabularyResponseForGrade(
            anyString());
        verify(vocabularyService, times(Grade.getGrades().length - 1)).saveVocabularies(anyList());
        verify(testService, times(Grade.getGrades().length - 1)).saveTests(anyList());
    }

    @Test
    void getGrammarExamplesQuery_정상적으로_모든_등급에_대해_실행된다() {
        // when
        schedulerService.getGrammarExamplesQuery();
        // then
        verify(alanAIService, times(Grade.getGrades().length)).getGrammarExamplesQuery(anyString());
    }

    @Test
    void getGrammarExamplesQuery_예외_발생시_다른_등급_처리를_계속한다() {
        // given
        doThrow(new RuntimeException("Test exception")).when(alanAIService)
            .getGrammarExamplesQuery("A1");
        // when
        schedulerService.getGrammarExamplesQuery();
        // then
        verify(alanAIService, times(Grade.getGrades().length)).getGrammarExamplesQuery(anyString());
    }
}