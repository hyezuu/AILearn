package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.essay.Essay;
import com.example.ormi5finalteam1.domain.essay.EssayGuide;
import com.example.ormi5finalteam1.domain.essay.dto.request.EssayRequestDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.EssayGuideResponseDto;
import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.EssayGuideRepository;
import com.example.ormi5finalteam1.repository.EssayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EssayService {

    private final EssayRepository essayRepository;
    private final EssayGuideRepository essayGuideRepository;

    /** 에세이 생성 */
    public void createEssay(EssayRequestDto essayRequestDto) {
        Essay essay = convertRequestToEntity(essayRequestDto);
        essayRepository.save(essay);
    }

    /** 에세이 수정 */
    public void updateEssay(Long id, EssayRequestDto essayRequestDto) {
        Essay essay = essayRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.ESSAY_NOT_FOUND));

        Essay updatedEssay = Essay.builder()
                .id(id)
                .user(essay.getUser())
                .topic(essayRequestDto.topic())
                .content(essayRequestDto.content())
                .build();
        essayRepository.save(updatedEssay);
    }

//    /** 에세이 첨삭 */
//    public void reviewEssay(Long id) {
//
//    }

    /** 에세이 작성 가이드 조회 */
    public List<EssayGuideResponseDto> showEssayGuide() {
        return essayGuideRepository.findAll()
                .stream()
                .map(this::convertGuideResponseToDto).collect(Collectors.toList());
    }

    private Essay convertRequestToEntity(EssayRequestDto essayRequestDto) {

      return Essay.builder()
              .user(new User(essayRequestDto.userId()))
              .topic(essayRequestDto.topic())
              .content(essayRequestDto.content())
              .build();
    }

    private EssayGuideResponseDto convertGuideResponseToDto(EssayGuide essayGuide) {
       return new EssayGuideResponseDto(
               essayGuide.getGrade(),
               essayGuide.getContent()
       );
    }

}
