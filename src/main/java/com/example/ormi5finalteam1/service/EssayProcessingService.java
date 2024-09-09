package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.essay.Essay;
import com.example.ormi5finalteam1.domain.essay.ReviewedEssays;
import com.example.ormi5finalteam1.domain.essay.dto.response.ReviewedEssaysResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.external.api.util.ContentParser;
import com.example.ormi5finalteam1.external.constants.AlanAIRequestPrompt;
import com.example.ormi5finalteam1.repository.ReviewedEssaysRepository;
import com.example.ormi5finalteam1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EssayProcessingService {
    private final EssayService essayService;
    private final EssayAlanApiService essayAlanApiService;
    private final ReviewedEssaysRepository reviewedEssaysRepository;
    private final ContentParser contentParser;
    private final UserRepository userRepository;

    @Value("${alanai.api.key}")
    private String clientId;

    @Transactional
    public ReviewedEssaysResponseDto processEssay(Long essayId, Provider provider) {
        /* 1. DB에서 Essay 가져오기 */
        Essay essay = essayService.getEssayById(essayId);
        if(!essay.getUser().getId().equals(provider.id())) { // 에세이 ID로 불러온 UserID와 현재 User의 ID가 같지 않으면 403
            throw new BusinessException((ErrorCode.ESSAY_EDIT_FORBIDDEN));
        }
        String sendEssayContent = essay.getContent() + AlanAIRequestPrompt.ESSAY_REVIEW_PROMPT.getPromptTemplate(); // AI에게 전송할 문장

        /* 2. Essay.content를 Alan에 보내고 응답 받기 */
        String feedback = essayAlanApiService.getApiResponse(sendEssayContent, clientId); // AI에게 받은 문장
        String pointFeedback = contentParser.parseEssayReviewResponse(feedback); // AI에게 받은 문장 파싱

        /* 3. 응답을 EssayFeedback 엔티티로 저장 */
        boolean isExistReview = reviewedEssaysRepository.existsByEssayId(essay.getId());
        ReviewedEssays reviewedEssays = saveReviewedEssays(isExistReview, essay, pointFeedback);
        reviewedEssaysRepository.save(reviewedEssays);

        /* 4. 해당 user의 포인트 3증가 */
        User user = essay.getUser();
        user.addEssayWriteAndReviewPoint();

        /* 5. ReviewedEssaysResponseDto로 첨삭된 데이터, 기존에세이 데이터 반환 */
        return ReviewedEssaysResponseDto.builder()
                .essayContent(reviewedEssays.getEssay().getContent())
                .reviewedContent(reviewedEssays.getContent())
                .build();

    }

    private ReviewedEssays saveReviewedEssays(boolean isExistReview, Essay essay, String pointFeedback) {
        if(isExistReview){ // 이미 첨삭을 받은 적이 있을 경우
            ReviewedEssays findByEssayId = reviewedEssaysRepository.findByEssayId(essay.getId());
            return ReviewedEssays.builder()
                    .id(findByEssayId.getId())
                    .content(pointFeedback)
                    .essay(essay)
                    .build();
        } else { // 해당 에세이를 처음 첨삭하는 경우
            return ReviewedEssays.builder()
                    .content(pointFeedback)
                    .essay(essay)
                    .build();
        }
    }
}
