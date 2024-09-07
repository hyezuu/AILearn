package com.example.ormi5finalteam1.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyListVocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.dto.MyVocabularyListResponseDto;
import com.example.ormi5finalteam1.repository.VocabularyListRepository;
import com.example.ormi5finalteam1.repository.VocabularyListVocabularyRepository;
import com.example.ormi5finalteam1.repository.VocabularyRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class VocabularyListServiceTest {

    @Mock
    private VocabularyListRepository vocabularyListRepository;

    @Mock
    private VocabularyListVocabularyRepository vocabularyListVocabularyRepository;

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private UserService userService;

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

    @Test
    void addVocabulary_로_유저의_단어장에_단어를_추가할_수_있다() {
        //given
        User user = mock(User.class);
        VocabularyList vocabularyList = mock(VocabularyList.class);
        List<Vocabulary> newVocabularies = Arrays.asList(
            new Vocabulary("word1", "meaning1", Grade.A1, "example1"),
            new Vocabulary("word2", "meaning2", Grade.A1, "example2")
        );

        when(userService.getUser(anyLong())).thenReturn(user);
        when(vocabularyListRepository.findByUserId(anyLong())).thenReturn(
            Optional.of(vocabularyList));
        when(vocabularyListVocabularyRepository.findMaxVocabularyIdByVocabularyListIdAndGrade(
            any(), any())).thenReturn(Optional.of(0L));
        when(vocabularyRepository.findTop10ByGradeAndIdGreaterThanOrderById(any(Grade.class),
            anyLong())).thenReturn(newVocabularies);
        //when
        vocabularyListService.addVocabulary(provider);
        //then
        verify(vocabularyListRepository).findByUserId(provider.id());
        verify(vocabularyListVocabularyRepository)
            .findMaxVocabularyIdByVocabularyListIdAndGrade(any(), any());
        verify(vocabularyRepository).findTop10ByGradeAndIdGreaterThanOrderById(any(), anyLong());
        verify(vocabularyList).addVocabularies(newVocabularies);
        verify(user).addWordToVocabularyPoint();
    }

    @Test
    void addVocabulary_는_단어장을_찾을_수_없을_시_BusinessException을_던진다() {
        //given
        User user = mock(User.class);
        when(vocabularyListRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        //when & then
        assertThatThrownBy(()-> vocabularyListService.addVocabulary(provider)).isInstanceOf(
            BusinessException.class).hasFieldOrPropertyWithValue("errorCode", ErrorCode.VOCABULARY_LIST_NOT_FOUND);
        verify(userService).getUser(anyLong());
        verify(vocabularyListVocabularyRepository,never()).findMaxVocabularyIdByVocabularyListIdAndGrade(any(),any());
        verify(vocabularyRepository,never()).findTop10ByGradeAndIdGreaterThanOrderById(any(), anyLong());
        verify(user, never()).addWordToVocabularyPoint();
    }

    @Test
    void addVocabulary_는_새로운_단어를_찾을_수_없는경우_BusinessException을_던진다() {
        //given
        User user = mock(User.class);
        VocabularyList vocabularyList = mock(VocabularyList.class);
        when(vocabularyListRepository.findByUserId(anyLong())).thenReturn(
            Optional.of(vocabularyList));
        when(vocabularyListVocabularyRepository.findMaxVocabularyIdByVocabularyListIdAndGrade(
            any(), any())).thenReturn(Optional.of(0L));
        when(vocabularyRepository.findTop10ByGradeAndIdGreaterThanOrderById(any(Grade.class),
            anyLong())).thenReturn(new ArrayList<>());
        //when & then
        assertThatThrownBy(()-> vocabularyListService.addVocabulary(provider))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NEW_VOCABULARIES_NOT_FOUND);
        verify(userService).getUser(anyLong());
        verify(vocabularyListRepository).findByUserId(provider.id());
        verify(vocabularyListVocabularyRepository).findMaxVocabularyIdByVocabularyListIdAndGrade(any(), any());
        verify(vocabularyList, never()).addVocabularies(any());
        verify(user, never()).addWordToVocabularyPoint();
    }

    @Test
    void getMyVocabularyList_는_유저_아이디로_단어장을_찾아올_수_있다() {
        // given
        when(vocabularyListRepository.findByUserId(anyLong())).thenReturn(Optional.of(vocabularyList));
        // when
        VocabularyList result = vocabularyListService.getMyVocabularyList(provider);
        // then
        assertThat(result).isEqualTo(vocabularyList);
    }

    @Test
    void getMyVocabularyList_는_단어장을_찾을_수_없는_경우_BusinessException_을_던진다() {
        // given
        when(vocabularyListRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> vocabularyListService.getMyVocabularyList(provider))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VOCABULARY_LIST_NOT_FOUND);
    }

    @Test
    void getMyVocabularies_는_페이징_된_단어_목록을_반환한다() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<VocabularyListVocabulary> vocabularyListVocabularies = Arrays.asList(
            new VocabularyListVocabulary(vocabularyList, new Vocabulary("word1", "meaning1", Grade.A1, "example1"),Grade.A1),
            new VocabularyListVocabulary(vocabularyList, new Vocabulary("word2", "meaning2", Grade.A1, "example2"),Grade.A1)
        );
        Page<VocabularyListVocabulary> vocabularyPage = new PageImpl<>(vocabularyListVocabularies, pageable, 2);

        when(vocabularyListRepository.findByUserIdOrderByCreatedAtDesc(provider.id(), pageable))
            .thenReturn(vocabularyPage);
        // when
        Page<MyVocabularyListResponseDto> result = vocabularyListService.getMyVocabularies(provider, pageable);
        // then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).word()).isEqualTo("word1");
        assertThat(result.getContent().get(1).word()).isEqualTo("word2");
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(vocabularyListRepository).findByUserIdOrderByCreatedAtDesc(provider.id(), pageable);
    }

    @Test
    void delete_는_단어장에서_단어를_삭제한다() {
        // given
        VocabularyListVocabulary vlv = mock(VocabularyListVocabulary.class);
        when(vocabularyListVocabularyRepository.findByIdAndVocabularyListUserId(anyLong(), anyLong()))
            .thenReturn(Optional.of(vlv));
        // when
        assertDoesNotThrow(() -> vocabularyListService.delete(provider, 1L));
        // then
        verify(vlv).delete();
        verify(vocabularyListVocabularyRepository).findByIdAndVocabularyListUserId(anyLong(), anyLong());
    }

    @Test
    void delete_는_단어를_찾을_수_없는_경우_BusinessException을_던진다() {
        // given
        when(vocabularyListVocabularyRepository.findByIdAndVocabularyListUserId(anyLong(), anyLong()))
            .thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> vocabularyListService.delete(provider, 1L))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VOCABULARY_NOT_FOUND);
        verify(vocabularyListVocabularyRepository).findByIdAndVocabularyListUserId(anyLong(), anyLong());
    }

}