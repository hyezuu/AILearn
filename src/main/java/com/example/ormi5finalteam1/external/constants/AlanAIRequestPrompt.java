package com.example.ormi5finalteam1.external.constants;

import lombok.Getter;

@Getter
public enum AlanAIRequestPrompt {
  // 문법 연습용 예문 단계별 문제 + insert query 요청
  GRAMMAR_EXAMPLES_INSERT_QUERY(
      "문법 연습용 예문 단계별 문제 insert query 요청",
      "영어 문법 예문 연습용 문제가 필요한데, {grade}단계 10문항 문제, 해답, 해설을 포함해서 만들고 insert 쿼리로 만들어줘. 테이블명은 grammar_examples이고 각 필드는 grade, question, answer, commentary 이렇게 있어");

  private final String description; // 요청의 목적을 설명하는 필드
  private final String promptTemplate; // 실제 프롬프트로 사용될 텍스트 템플릿 필드

  // 생성자
  AlanAIRequestPrompt(String description, String promptTemplate) {
    this.description = description;
    this.promptTemplate = promptTemplate;
  }

  // 변수를 삽입한 프롬프트를 반환하는 메소드
  public String getPrompt(String... variables) {
    String prompt = promptTemplate;
    for (int i = 0; i < variables.length; i++) {
      prompt = prompt.replace("{" + i + "}", variables[i]);
    }
    return prompt;
  }
}
