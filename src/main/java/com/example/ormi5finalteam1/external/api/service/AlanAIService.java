package com.example.ormi5finalteam1.external.api.service;

import com.example.ormi5finalteam1.external.api.client.AlanAIClient;
import com.example.ormi5finalteam1.external.api.dto.BaseResponse;
import com.example.ormi5finalteam1.external.api.util.ContentParser;
import com.example.ormi5finalteam1.external.constants.AlanAIRequestPrompt;
import com.example.ormi5finalteam1.repository.GrammarExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlanAIService {
  private final AlanAIClient alanAIClient;
  private final ContentParser contentParser;
  private final GrammarExampleRepository grammarExampleRepository;

  public void getGrammarExamplesQuery(AlanAIRequestPrompt prompt, String... grades) {
    // 레벨별로 요청
    BaseResponse response =
        alanAIClient.sendRequestToAlanAI(prompt.GRAMMAR_EXAMPLES_INSERT_QUERY, grades);

    // 응답 파싱
    String rawQuery = contentParser.parseGrammarExamplesQueryResponse(response.getContent());

    // 결과 출력
    if (rawQuery != null) {
      // todo: 예외처리
    } else {
      // todo: 예외처리
    }

    // grammar_examples 테이블에 raw query 날려서 데이터 생성
    grammarExampleRepository.insertGrammarExampleWithRawQuery(rawQuery);
  }
}
