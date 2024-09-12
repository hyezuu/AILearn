package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.essay.Essay;
import com.example.ormi5finalteam1.domain.essay.dto.request.EssayRequestDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.EssayGuideResponseDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.EssayResponseDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.ReviewedEssaysResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.EssayProcessingService;
import com.example.ormi5finalteam1.service.EssayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EssayController {

    private final EssayService essayService;
    private final EssayProcessingService essayProcessingService;

    /** 에세이 생성 */
    @PostMapping("/essays")
    public ResponseEntity<Void> createEssay(@Valid @RequestBody EssayRequestDto essayRequestDto) {
        essayService.createEssay(essayRequestDto);
        return new ResponseEntity<>(HttpStatusCode.valueOf(201));
    }

    /** 에세이 수정 */
    @PutMapping("/essays/{id}")
    public ResponseEntity<Void> updateEssay(@PathVariable Long id, @Valid @RequestBody EssayRequestDto essayRequestDto) {
        essayService.updateEssay(id, essayRequestDto);
        return new ResponseEntity<>(HttpStatusCode.valueOf(204));
    }

    /** 에세이 첨삭 */
    @PutMapping("/essays/{id}/review")
    public ResponseEntity<ReviewedEssaysResponseDto> reviewEssay(@PathVariable Long id, @AuthenticationPrincipal Provider provider) {
        ReviewedEssaysResponseDto reviewedEssay = essayProcessingService.processEssay(id, provider);
        return new ResponseEntity<>(reviewedEssay, HttpStatusCode.valueOf(200));
    }

    /** 에세이 작성 가이드 조회 */
    @GetMapping("/essay-guides")
    public ResponseEntity<List<EssayGuideResponseDto>> showEssayGuide() {
        List<EssayGuideResponseDto> essayGuideResponseDtoList = essayService.showEssayGuide();
        return new ResponseEntity<>(essayGuideResponseDtoList, HttpStatusCode.valueOf(200));
    }

    /** 내 에세이 조회 */
    @GetMapping("/me/essays")
    public ResponseEntity<Page<EssayResponseDto>> showMyEssays(
            @AuthenticationPrincipal Provider provider,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Page<EssayResponseDto> essayResponseDtoPages = essayService.showMyEssays(provider, page, pageSize);
        return new ResponseEntity<>(essayResponseDtoPages, HttpStatusCode.valueOf(200));
    }

    /** 내 에세이 상세 조회 */
    @GetMapping("/essays/{id}")
    public ResponseEntity<EssayResponseDto> showEssay(
            @PathVariable Long id,
            @AuthenticationPrincipal Provider provider
    ) {
        EssayResponseDto essayResponseDto = essayService.showEssay(id, provider);
        return new ResponseEntity<>(essayResponseDto, HttpStatusCode.valueOf(200));
    }

    /** 에세이 삭제 */
    @DeleteMapping("/essays/{id}")
    public ResponseEntity<Void> deleteEssay(@PathVariable Long id, @AuthenticationPrincipal Provider provider) {
        essayService.deleteEssay(id, provider);
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    /** 에세이 검색 */
    @GetMapping("/essays/search")
    public ResponseEntity<Page<EssayResponseDto>> searchEssays(
            @AuthenticationPrincipal Provider provider,
            @RequestParam(name = "topic") String topic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Page<EssayResponseDto> findByTopic = essayService.searchEssays(topic, page, pageSize, provider);
        return new ResponseEntity<>(findByTopic, HttpStatusCode.valueOf(200));
    }
}
