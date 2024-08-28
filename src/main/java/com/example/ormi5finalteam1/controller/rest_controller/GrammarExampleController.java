package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleResponseDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.MultipleGrammarExampleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GrammarExampleController {

  //    private final GrammarExampleService grammarExampleService;

  /** 문법 예문 생성 */
  @PostMapping("/grammar-examples")
  public ResponseEntity<MultipleGrammarExampleResponseDto> createGrammarExamples() {
    // 현재 로그인된 유저 정보 접근 로직
  }

  /** 문법 예문 조회 */
  @GetMapping("/me/grammar-examples")
  public ResponseEntity<MultipleGrammarExampleResponseDto> getGrammarExamples() {
    // 현재 로그인된 유저 정보 접근 로직
  }

  /** 문법 예문 채점 */
  @PostMapping("/grammar-examples/{id}/grading")
  public ResponseEntity<GrammarExampleResponseDto> gradeGrammarExample(@PathVariable Long id) {
    // 현재 로그인된 유저 정보 접근 로직
  }

  /** 문법 예문 추가 생성 */
  @PostMapping("/grammar-examples/more")
  public ResponseEntity<MultipleGrammarExampleResponseDto> createMoreGrammarExamples() {
    // 현재 로그인된 유저 정보 접근 로직
  }
}
