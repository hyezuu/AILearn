package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleGradingDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleResponseDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.MultipleGrammarExampleResponseDto;
import com.example.ormi5finalteam1.service.GrammarExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GrammarExampleController {

      private final GrammarExampleService grammarExampleService;

  /** 문법 예문 조회 */
  @GetMapping("/me/grammar-examples")
  public ResponseEntity<MultipleGrammarExampleResponseDto> getGrammarExamples() {
    // 현재 로그인된 유저 정보에서 grade, grammarExampleCount를 추출하여 조회
  }

  /** 문법 예문 채점 */
  @PostMapping("/grammar-examples/{id}/grading")
  public ResponseEntity<GrammarExampleGradingDto> gradeGrammarExample(@PathVariable Long id) {
    // 현재 로그인된 유저 정보에서 grade를 추출
  }

  /** 문법 예문 추가 */
  @PostMapping("/grammar-examples/more")
  public ResponseEntity<MultipleGrammarExampleResponseDto> createMoreGrammarExamples() {
    // 현재 로그인된 유저 정보에서 grade, grammarExampleCount를 추출
    // 해당 유저의 grammarExampleCount를 +5
  }
}
