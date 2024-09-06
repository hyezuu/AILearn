package com.example.ormi5finalteam1.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.repository.VocabularyRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VocabularyServiceTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @InjectMocks
    private VocabularyService vocabularyService;

    List<Vocabulary> vocabularies;

    @BeforeEach
    void setUp() {
        vocabularies = new ArrayList<>();
        vocabularies.add(new Vocabulary("apple", "사과", Grade.A1, "An apple a day keeps the doctor away."));
        vocabularies.add(new Vocabulary("banana", "바나나", Grade.A1, "I like to eat bananas."));
        vocabularies.add(new Vocabulary("cherry", "체리", Grade.A2, "She picked some cherries from the tree."));
    }

    @Test
    void saveVocabularies_단어들을_저장할_수_있다() {
        //given
        when(vocabularyRepository.existsByWord(anyString())).thenReturn(false);
        when(vocabularyRepository.saveAll(anyList())).thenReturn(vocabularies);
        //when
        vocabularyService.saveVocabularies(vocabularies);
        //then
        verify(vocabularyRepository).saveAll(anyList());
        verify(vocabularyRepository, times(vocabularies.size())).existsByWord(anyString());
    }

    @Test
    void saveVocabularies_는_중복된_단어를_제외하고_저장한다() {
        // given
        when(vocabularyRepository.existsByWord("apple")).thenReturn(true);
        when(vocabularyRepository.existsByWord("banana")).thenReturn(false);
        when(vocabularyRepository.existsByWord("cherry")).thenReturn(false);
        // when
        vocabularyService.saveVocabularies(vocabularies);
        // then
        verify(vocabularyRepository, times(3)).existsByWord(anyString());
        verify(vocabularyRepository).saveAll(argThat((List<Vocabulary> list) -> list.size() == 2));
    }

    @Test
    void saveVocabularies_모든_단어가_중복일_경우() {
        // given
        when(vocabularyRepository.existsByWord(any())).thenReturn(true);
        // when
        vocabularyService.saveVocabularies(vocabularies);
        // then
        verify(vocabularyRepository, times(3)).existsByWord(any());
        verify(vocabularyRepository).saveAll(any());
    }
}