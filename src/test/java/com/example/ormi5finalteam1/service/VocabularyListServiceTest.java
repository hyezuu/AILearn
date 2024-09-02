package com.example.ormi5finalteam1.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import com.example.ormi5finalteam1.repository.VocabularyListRepository;
import com.example.ormi5finalteam1.repository.VocabularyListVocabularyRepository;
import com.example.ormi5finalteam1.repository.VocabularyRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VocabularyListServiceTest {

    @Mock
    private VocabularyListRepository vocabularyListRepository;

    @Mock
    private VocabularyListVocabularyRepository vocabularyListVocabularyRepository;

    @Mock
    private VocabularyRepository vocabularyRepository;

    @InjectMocks
    private VocabularyListService vocabularyListService;

    private Provider provider;
    private VocabularyList vocabularyList;

    @BeforeEach
    void setUp() {
        provider = new Provider(1L, "test@test.com", "nickname", null, Grade.A1, 10);
        vocabularyList = new VocabularyList(new User(provider.id()));
    }

    @Test
    void create_로_단어장을_생성할_수_있다() {
        //given
        //when
        vocabularyListService.create(provider);
        //then
        verify(vocabularyListRepository).save(any(VocabularyList.class));
    }




}