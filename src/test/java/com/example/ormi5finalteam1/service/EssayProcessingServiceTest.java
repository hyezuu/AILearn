package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.essay.Essay;
import com.example.ormi5finalteam1.domain.essay.ReviewedEssays;
import com.example.ormi5finalteam1.domain.essay.dto.response.ReviewedEssaysResponseDto;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.external.api.util.ContentParser;
import com.example.ormi5finalteam1.repository.ReviewedEssaysRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EssayProcessingServiceTest {

    @Mock
    private EssayService essayService;

    @Mock
    private EssayAlanApiService essayAlanApiService;

    @Mock
    private ReviewedEssaysRepository reviewedEssaysRepository;

    @Mock
    private ContentParser contentParser;

    @InjectMocks
    private EssayProcessingService essayProcessingService;

    private Essay essay;
    private ReviewedEssays reviewedEssays;

    @BeforeEach
    void setUp() {
        User user = new User(1L);
        essay = new Essay(1L, user, "Test Topic", "Test Content");

        reviewedEssays = ReviewedEssays.builder()
                .id(1L)
                .essay(essay)
                .content("Reviewed Content")
                .build();

        ReflectionTestUtils.setField(essayProcessingService, "clientId", "test-client-id");
    }

    @Test
    void processEssay_는_첨삭된_에세이를_반환한다() {
        // Given
        when(essayService.getEssayById(1L)).thenReturn(essay);
        when(essayAlanApiService.getApiResponse(anyString(), anyString())).thenReturn("AI Feedback");
        when(contentParser.parseEssayReviewResponse(anyString())).thenReturn("Parsed Feedback");
        when(reviewedEssaysRepository.existsByEssayId(1L)).thenReturn(false);
        when(reviewedEssaysRepository.save(any(ReviewedEssays.class))).thenReturn(reviewedEssays);

        // When
        ReviewedEssaysResponseDto response = essayProcessingService.processEssay(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEssayContent()).isEqualTo(essay.getContent());
        assertThat(response.getReviewedContent()).isEqualTo("Parsed Feedback");

        verify(essayService).getEssayById(1L);
        verify(essayAlanApiService).getApiResponse(anyString(), anyString());
        verify(contentParser).parseEssayReviewResponse("AI Feedback");
        verify(reviewedEssaysRepository).existsByEssayId(essay.getId());
        verify(reviewedEssaysRepository).save(any(ReviewedEssays.class));
    }

    @Test
    void processEssay_이미_첨삭된_에세이를_업데이트_후_반환한다() {
        // Given
        when(essayService.getEssayById(anyLong())).thenReturn(essay);
        when(essayAlanApiService.getApiResponse(anyString(), anyString())).thenReturn("AI Feedback");
        when(contentParser.parseEssayReviewResponse(anyString())).thenReturn("Parsed Feedback");
        when(reviewedEssaysRepository.existsByEssayId(anyLong())).thenReturn(true);
        when(reviewedEssaysRepository.findByEssayId(anyLong())).thenReturn(reviewedEssays);
        when(reviewedEssaysRepository.save(any(ReviewedEssays.class))).thenReturn(reviewedEssays);

        // When
        ReviewedEssaysResponseDto response = essayProcessingService.processEssay(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEssayContent()).isEqualTo(essay.getContent());
        assertThat(response.getReviewedContent()).isEqualTo("Parsed Feedback");

        verify(essayService).getEssayById(1L);
        verify(essayAlanApiService).getApiResponse(anyString(), anyString());
        verify(contentParser).parseEssayReviewResponse("AI Feedback");
        verify(reviewedEssaysRepository).existsByEssayId(essay.getId());
        verify(reviewedEssaysRepository).findByEssayId(essay.getId());
        verify(reviewedEssaysRepository).save(any(ReviewedEssays.class));
    }
}
