package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.essay.Essay;
import com.example.ormi5finalteam1.domain.essay.ReviewedEssays;
import com.example.ormi5finalteam1.domain.essay.dto.response.ReviewedEssaysResponseDto;
import com.example.ormi5finalteam1.repository.ReviewedEssaysRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EssayProcessingService {
    private final EssayService essayService;
    private final EssayAlanApiService essayAlanApiService;
    private final ReviewedEssaysRepository reviewedEssaysRepository;

    public ReviewedEssaysResponseDto processEssay(Long essayId) {
        String clientId = "62f951f4-38be-45ee-9d9c-16885b4a098a";
        /* 1. DB에서 Essay 가져오기 */
        Essay essay = essayService.getEssayById(essayId);
        String sendEssayContent = "[" + essay.getContent() + "]라는 영어 에세이를 작성했는데 첨삭해주고 한글로 간단하게 설명해줘. 너의 답변은 [첨삭된 문장: 첨삭 된 내용글, 설명: 설명] 이 포맷으로 되어있어야해. 물론입니다 이딴말은 하지마"; // AI에게 전송할 문장

        /* 2. Essay.content를 Alan에 보내고 응답 받기 */
        String feedback = essayAlanApiService.getApiResponse(sendEssayContent, clientId); // AI에게 받은 문장
        int startIndex = feedback.lastIndexOf("'content': ") + 12; // 마지막 'content': 에 답변이 존재함
        int endIndex = feedback.length() - 7;
        String pointFeedback = feedback.substring(startIndex,endIndex);

        /* 3. 응답을 EssayFeedback 엔티티로 저장 */
        boolean isExistReview = reviewedEssaysRepository.existsByEssayId(essay.getId());
        ReviewedEssays reviewedEssays = saveReviewedEssays(isExistReview, essay, pointFeedback);
        reviewedEssaysRepository.save(reviewedEssays);

        /* 4. ReviewedEssaysResponseDto로 첨삭된 데이터, 기존에세이 데이터 반환 */
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
