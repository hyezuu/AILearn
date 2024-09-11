package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.essay.Essay;
import com.example.ormi5finalteam1.domain.essay.EssayGuide;
import com.example.ormi5finalteam1.domain.essay.dto.request.EssayRequestDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.EssayGuideResponseDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.EssayResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.EssayGuideRepository;
import com.example.ormi5finalteam1.repository.EssayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EssayServiceTest {

    @Mock
    private EssayRepository essayRepository;

    @Mock
    private EssayGuideRepository essayGuideRepository;

    @InjectMocks
    private EssayService essayService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L);
    }

    @Test
    void createEssay_은_에세이를_생성할_수_있다() {
        //given
        EssayRequestDto essayRequestDto = new EssayRequestDto(user.getId(), "TestTopic", "TestContent");
        Essay newEssay = new Essay(1L,user,"TestTopic","TestContent");
        when(essayRepository.save(any(Essay.class))).thenReturn(newEssay);

        //when & then
        assertThatCode(() -> essayService.createEssay(essayRequestDto)).doesNotThrowAnyException(); //메서드가 호출될 때 예외가 발생하지 않음을 확인
        verify(essayRepository).save(any(Essay.class)); // createEssay 메서드가 Essay 객체를 저장하려고 시도했는지를 검증
    }

    @Test
    void updateEssay_은_에세이를_수정할_수_있다() {
        //given
        Essay existingEssay = new Essay(1L,user,"TestTopic","TestContent");
        Essay updatedEssay = new Essay(1L,user,"TestUpdatedTopic","TestUpdatedContent");
        EssayRequestDto essayUpdateRequestDto = new EssayRequestDto(user.getId(), "TestUpdatedTopic", "TestUpdatedContent");

        when(essayRepository.findById(1L)).thenReturn(Optional.of(existingEssay));
        when(essayRepository.save(any(Essay.class))).thenReturn(updatedEssay);

        //when
        Essay result = essayService.updateEssay(1L, essayUpdateRequestDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getTopic()).isEqualTo("TestUpdatedTopic");
        assertThat(result.getContent()).isEqualTo("TestUpdatedContent");
        verify(essayRepository, times(1)).findById(1L); // 정확히 한 번 호출되었는지 검증
        verify(essayRepository, times(1)).save(any(Essay.class));
    }

    @Test
    void showEssayGuide_는_에세이_가이드를_조회할_수_있다() {
        // given
        List<EssayGuide> guides = Arrays.asList(
                new EssayGuide(1L, Grade.A1, "Guide 1"),
                new EssayGuide(2L, Grade.A2, "Guide 2")
        );
        when(essayGuideRepository.findAll()).thenReturn(guides);
        // when
        List<EssayGuideResponseDto> result = essayService.showEssayGuide();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getContent()).isEqualTo("Guide 1");
        assertThat(result.get(0).getGrade()).isEqualTo(Grade.A1);
        assertThat(result.get(1).getContent()).isEqualTo("Guide 2");
        verify(essayGuideRepository, times(1)).findAll();
    }

    @Test
    void showMyEssays_는_에세이를_페이지_단위로_조회할_수_있다() {
        // given
        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        List<Essay> essays = Arrays.asList(
                new Essay(1L, user, "Topic 1", "Content 1"),
                new Essay(2L, user, "Topic 2", "Content 2"),
                new Essay(3L, user, "Topic 3", "Content 3")
        );
        Page<Essay> essayPage = new PageImpl<>(essays, pageable, essays.size());
        Provider provider = new Provider(user.getId(), "email@email.com", "nickname", Role.ADMIN, Grade.A2, 0);

        when(essayRepository.findByUserIdAndDeletedAtNull(user.getId(), pageable)).thenReturn(essayPage);

        // when
        Page<EssayResponseDto> result = essayService.showMyEssays(provider, 0, 3);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getTopic()).isEqualTo("Topic 1");
        assertThat(result.getContent().get(1).getTopic()).isEqualTo("Topic 2");
        assertThat(result.getContent().get(2).getTopic()).isEqualTo("Topic 3");
        verify(essayRepository, times(1)).findByUserIdAndDeletedAtNull(user.getId(), pageable);

    }

    @Test
    void showMyEssays_페이지네이션이_잘_작동하는지_확인한다() {
        // given
        Pageable firstPageRequest = PageRequest.of(0, 2,  Sort.by("createdAt").descending());
        Pageable secondPageRequest = PageRequest.of(1, 2,  Sort.by("createdAt").descending());

        List<Essay> essaysPage1 = Arrays.asList(
                new Essay(1L, user, "Topic 1", "Content 1"),
                new Essay(2L, user, "Topic 2", "Content 2")
        );
        List<Essay> essaysPage2 = Arrays.asList(
                new Essay(3L, user, "Topic 3", "Content 3")
        );

        Page<Essay> firstPage = new PageImpl<>(essaysPage1, firstPageRequest, 3);
        Page<Essay> secondPage = new PageImpl<>(essaysPage2, secondPageRequest, 3);

        Provider provider = new Provider(user.getId(), "email@email.com","nickname", Role.ADMIN, Grade.A2, 0);

        when(essayRepository.findByUserIdAndDeletedAtNull(user.getId(), firstPageRequest)).thenReturn(firstPage);
        when(essayRepository.findByUserIdAndDeletedAtNull(user.getId(), secondPageRequest)).thenReturn(secondPage);

        // when
        Page<EssayResponseDto> resultPage1 = essayService.showMyEssays(provider, 0, 2);
        Page<EssayResponseDto> resultPage2 = essayService.showMyEssays(provider, 1, 2);

        // then
        // 첫 번째 페이지 검증
        assertThat(resultPage1).isNotNull();
        assertThat(resultPage1.getContent()).hasSize(2);
        assertThat(resultPage1.getContent().get(0).getTopic()).isEqualTo("Topic 1");
        assertThat(resultPage1.getContent().get(1).getTopic()).isEqualTo("Topic 2");

        // 두 번째 페이지 검증
        assertThat(resultPage2).isNotNull();
        assertThat(resultPage2.getContent()).hasSize(1);
        assertThat(resultPage2.getContent().get(0).getTopic()).isEqualTo("Topic 3");

        // 페이지 전체 검증
        assertThat(resultPage1.getTotalElements()).isEqualTo(3);
        assertThat(resultPage1.getTotalPages()).isEqualTo(2); // 전체 페이지 수는 2가 되어야 함
        assertThat(resultPage2.getTotalPages()).isEqualTo(2);

        verify(essayRepository, times(1)).findByUserIdAndDeletedAtNull(user.getId(), firstPageRequest);
        verify(essayRepository, times(1)).findByUserIdAndDeletedAtNull(user.getId(), secondPageRequest);
    }

    @Test
    void showMyEssays_는_에세이가_없으면_빈_페이지를_반환한다() {
        // given
        Pageable pageable = PageRequest.of(0, 3,  Sort.by("createdAt").descending());
        Page<Essay> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
        Provider provider = new Provider(user.getId(), "email@email.com","nickname", Role.ADMIN, Grade.A2, 0);
        when(essayRepository.findByUserIdAndDeletedAtNull(user.getId(), pageable)).thenReturn(emptyPage);

        // when
        Page<EssayResponseDto> result = essayService.showMyEssays(provider, 0, 3);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
        verify(essayRepository, times(1)).findByUserIdAndDeletedAtNull(user.getId(), pageable);
    }

    @Test
    void getEssayById_는_id로_에세이를_조회할_수_있다() {
        // given
        Essay essay = new Essay(1L, user, "TestTopic", "TestContent");
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay));

        // when
        Essay result = essayService.getEssayById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTopic()).isEqualTo("TestTopic");
        assertThat(result.getContent()).isEqualTo("TestContent");
        verify(essayRepository, times(1)).findById(1L);
    }

    @Test
    void getEssayById_는_에세이를_못찾으면_예외를_던진다() {
        // given
        Long essayId = 1L;
        when(essayRepository.findById(essayId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> essayService.getEssayById(essayId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Essay not found");
        verify(essayRepository, times(1)).findById(essayId);
    }


}
