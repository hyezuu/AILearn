package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.grammar_example.GrammarExample;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleDto;
import com.example.ormi5finalteam1.domain.grammar_example.dto.GrammarExampleGradingDto;
import com.example.ormi5finalteam1.repository.GrammarExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GrammarExampleService {
  private final GrammarExampleRepository grammarExampleRepository;
  private final UserPointService userPointService;
  private final UserInfoService userInfoService;

  /** 문법 예문 조회 */
  public List<GrammarExampleDto> getGrammarExamples(
      Grade grade, Pageable pageable, String keyword) {
    Page<GrammarExample> grammarExamples;
    PageRequest pageRequest =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").descending());

    // 키워드 검색조건이 있을 때
    if (keyword != null) {
      grammarExamples =
          grammarExampleRepository.findByQuestionContainingAndGradeOrderByIdAsc(keyword, grade, pageRequest);
    } else {
      grammarExamples = grammarExampleRepository.findByGradeOrderByIdAsc(grade, pageRequest);
    }

    if (grammarExamples.isEmpty()) {
      throw new BusinessException(ErrorCode.GRAMMAR_EXAMPLES_NOT_FOUND);
    }

    return grammarExamples.stream()
        .map(GrammarExampleService::convertToDto)
        .collect(Collectors.toList());
  }

  /** 문법 예문 채점 */
  public GrammarExampleGradingDto gradeGrammarExample(Long id, Long userId, String answer) {
    GrammarExampleGradingDto grammarExampleGradingDto = new GrammarExampleGradingDto();
    GrammarExampleDto grammarExampleDto =
        grammarExampleRepository
            .findById(id)
            .map(GrammarExampleService::convertToDto)
            .orElseThrow(() -> new IllegalArgumentException()); // todo: exception 교체

    if (!grammarExampleDto.getAnswer().equals(answer)) {
      grammarExampleGradingDto.setCorrect(false);
    } else {
      grammarExampleGradingDto.setCorrect(true);

      // 정답일 경우 사용자 경험치 포인트 상승
      userPointService.addPointsToUser(userId, 1); // todo: 포인트 상수관리
    }

    grammarExampleGradingDto.setData(grammarExampleDto);

    return grammarExampleGradingDto;
  }

  /** 문법 예문 추가 */
  public void createMoreGrammarExamples(Long userId) {
    // 해당 유저의 grammarExampleCount를 +5
    userInfoService.modifyGrammarExampleCountToUser(userId);
  }

  /**
   * dto -> entity
   *
   * @param GrammarExampleDto
   * @return GrammarExample
   */
  private static GrammarExample convertToEntity(GrammarExampleDto grammarExampleDto) {
    Grade grade = grammarExampleDto.getGrade();
    String question = grammarExampleDto.getQuestion();
    String answer = grammarExampleDto.getAnswer();
    String commentary = grammarExampleDto.getCommentary();

    GrammarExample grammarExample = new GrammarExample(grade, question, answer, commentary);

    return grammarExample;
  }

  /**
   * entity -> dto
   *
   * @param GrammarExample
   * @return GrammarExampleDto
   */
  private static GrammarExampleDto convertToDto(GrammarExample grammarExample) {
    Long id = grammarExample.getId();
    Grade grade = grammarExample.getGrade();
    String question = grammarExample.getQuestion();
    String answer = grammarExample.getAnswer();
    String commentary = grammarExample.getCommentary();
    LocalDateTime createdAt = grammarExample.getCreatedAt();

    GrammarExampleDto grammarExampleDto =
        new GrammarExampleDto(id, grade, question, answer, commentary, createdAt);

    return grammarExampleDto;
  }
}
