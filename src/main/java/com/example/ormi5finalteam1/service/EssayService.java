package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.essay.Essay;
import com.example.ormi5finalteam1.domain.essay.EssayGuide;
import com.example.ormi5finalteam1.domain.essay.dto.request.EssayRequestDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.EssayGuideResponseDto;
import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.essay.dto.response.EssayResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.EssayGuideRepository;
import com.example.ormi5finalteam1.repository.EssayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EssayService {

    private final EssayRepository essayRepository;
    private final EssayGuideRepository essayGuideRepository;

    /** 에세이 생성 */
    @Transactional
    public void createEssay(EssayRequestDto essayRequestDto) {
        Essay essay = convertRequestToEntity(essayRequestDto);
        essayRepository.save(essay);
    }

    /** 에세이 수정 */
    @Transactional
    public Essay updateEssay(Long id, EssayRequestDto essayRequestDto) {
        Essay essay = essayRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.ESSAY_NOT_FOUND));

        if(!essayRequestDto.userId().equals(essay.getUser().getId())) {
            throw new BusinessException((ErrorCode.ESSAY_EDIT_FORBIDDEN));
        }
            Essay updatedEssay = Essay.builder()
                    .id(id)
                    .user(essay.getUser())
                    .topic(essayRequestDto.topic())
                    .content(essayRequestDto.content())
                    .build();
            return essayRepository.save(updatedEssay);
    }

    /** 에세이 삭제 */
    @Transactional
    public void deleteEssay(Long id, Provider provider) {
        Essay essay = getEssayById(id);

        if(!essay.getUser().getId().equals(provider.id())) {
            throw new BusinessException((ErrorCode.ESSAY_EDIT_FORBIDDEN));
        }
        essay.delete();
    }

    /** 에세이 작성 가이드 조회 */
    public List<EssayGuideResponseDto> showEssayGuide() {
        return essayGuideRepository.findAll()
                .stream()
                .map(this::convertGuideResponseToDto).collect(Collectors.toList());
    }

    /** 내 에세이 목록 조회 */
    public Page<EssayResponseDto> showMyEssays(Provider provider, int page, int pageSize) {
            Pageable pageable = PageRequest.of(page,pageSize, Sort.by("createdAt").descending());
            Page<Essay> essayByUserId = essayRepository.findByUserIdAndDeletedAtNull(provider.id(), pageable);
            return essayByUserId.map(this::convertResponseToDto);
    }

    /** 에세이 상세 조회 */
    public EssayResponseDto showEssay(Long id, Provider provider) {
        Essay essay = getEssayById(id);

        if(!essay.getUser().getId().equals(provider.id())) { // 사용자 권한 확인 403
            throw new BusinessException((ErrorCode.ESSAY_EDIT_FORBIDDEN));
        }

        if(essay.getDeletedAt() != null) { // 삭제된 에세이 404
            throw new BusinessException((ErrorCode.ESSAY_NOT_FOUND));
        }

        return convertResponseToDto(essay);
    }

    /** 에세이 검색 */
    public Page<EssayResponseDto> searchEssays(String topic, int page, int pageSize, Provider provider) {
        Pageable pageable = PageRequest.of(page,pageSize, Sort.by("createdAt").descending());
        Page<Essay> byTopicContains = essayRepository.findByTopicContainsAndUser_IdAndDeletedAtNull(topic, provider.id(), pageable);
        return byTopicContains.map(this::convertResponseToDto);
    }

    /** DTO-Entity 변환 */
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

    private EssayResponseDto convertResponseToDto(Essay essay) {
        return EssayResponseDto.builder()
                .id(essay.getId())
                .topic(essay.getTopic())
                .content(essay.getContent())
                .createdAt(essay.getCreatedAt())
                .build();
    }

    /** id로 에세이 조회 */
    public Essay getEssayById(Long id) {
        return essayRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ESSAY_NOT_FOUND));
    }
}
