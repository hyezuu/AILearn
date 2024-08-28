package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleGradingDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.request.GradeGrammarExampleRequestDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.response.MultipleGrammarExampleResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.GrammarExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GrammarExampleController {

  private final GrammarExampleService grammarExampleService;

  /** 문법 예문 조회 */
  @GetMapping("/me/grammar-examples")
  public ResponseEntity<MultipleGrammarExampleResponseDto> getGrammarExamples(
      @AuthenticationPrincipal Provider provider,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String keyword) {
    // 현재 로그인된 유저 정보에서 grade, grammarExampleCount를 추출하여 조회

    PageRequest pageRequest = PageRequest.of(page, pageSize);

    if (provider == null) {
      // todo: 예외처리
    }

    Grade grade = provider.grade();
    int grammarExampleCount = provider.grammarExampleCount();

    // 1페이지 외의 페이지 조회시
    if (page > 1) {
      pageSize = grammarExampleCount % 10;
    }
    List<GrammarExampleDto> grammarExamples =
        grammarExampleService.getGrammarExamples(grade, pageRequest, keyword);

    MultipleGrammarExampleResponseDto responseDto =
        new MultipleGrammarExampleResponseDto(grammarExamples, grammarExamples.size());

    return new ResponseEntity<>(responseDto, HttpStatusCode.valueOf(200));
  }

  /** 문법 예문 채점 */
  @PostMapping("/grammar-examples/{id}/grading")
  public ResponseEntity<GrammarExampleGradingDto> gradeGrammarExample(
      @PathVariable Long id,
      @RequestParam GradeGrammarExampleRequestDto requestDto,
      @AuthenticationPrincipal Provider provider) {
    Long userId = provider.id();
    String answer = requestDto.getAnswer();
    GrammarExampleGradingDto grammarExampleGradingDto =
        grammarExampleService.gradeGrammarExample(id, userId, answer);

    return new ResponseEntity<>(grammarExampleGradingDto, HttpStatusCode.valueOf(200));
  }

  /** 문법 예문 추가 */
  @PostMapping("/grammar-examples/more")
  public ResponseEntity<MultipleGrammarExampleResponseDto> createMoreGrammarExamples() {
    // 현재 로그인된 유저 정보에서 grade, grammarExampleCount를 추출
    // 해당 유저의 grammarExampleCount를 +5
  }
}
