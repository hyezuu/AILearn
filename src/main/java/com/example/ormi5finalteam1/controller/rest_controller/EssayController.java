package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.essay.dto.request.EssayRequestDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.EssayGuideResponseDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.ReviewedEssaysResponseDto;
import com.example.ormi5finalteam1.service.EssayProcessingService;
import com.example.ormi5finalteam1.service.EssayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReviewedEssaysResponseDto> reviewEssay(@PathVariable Long id) {
        ReviewedEssaysResponseDto reviewedEssay = essayProcessingService.processEssay(id);
        return new ResponseEntity<>(reviewedEssay, HttpStatusCode.valueOf(200));
    }

    /** 에세이 작성 가이드 조회 */
    @GetMapping("/essay-guides")
    public ResponseEntity<List<EssayGuideResponseDto>> showEssayGuide() {
        List<EssayGuideResponseDto> essayGuideResponseDtoList = essayService.showEssayGuide();
        return new ResponseEntity<>(essayGuideResponseDtoList, HttpStatusCode.valueOf(200));
    }
}
