package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleGradingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GrammarExampleService {
//      private final GrammarExampleRepository grammarExampleRepository;

  /** 문법 예문 조회 */
  public List<GrammarExampleDto> getGrammarExamples(Grade grade, int grammarExampleCount) {}

  /** 문법 예문 채점 */
  public GrammarExampleGradingDto gradeGrammarExample(Long id, String answer) {}

  /** 문법 예문 추가 */
  public List<GrammarExampleDto> createMoreGrammarExamples(Grade grade, int grammarExampleCount) {}
}
