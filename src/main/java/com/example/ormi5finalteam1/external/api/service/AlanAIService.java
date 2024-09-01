package com.example.ormi5finalteam1.external.api.service;

import com.example.ormi5finalteam1.external.api.client.AlanAIClient;
import com.example.ormi5finalteam1.external.api.dto.BaseResponse;
import com.example.ormi5finalteam1.external.api.util.ContentParser;
import com.example.ormi5finalteam1.external.constants.AlanAIRequestPrompt;
import com.example.ormi5finalteam1.repository.GrammarExampleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlanAIService {

  private final AlanAIClient alanAIClient;
  private final ContentParser contentParser;
  private final GrammarExampleRepository grammarExampleRepository;
  private final ObjectMapper objectMapper;

  public void getGrammarExamplesQuery(String... grades) {
    // 레벨별로 요청
    BaseResponse response =
        alanAIClient.sendRequestToAlanAI(AlanAIRequestPrompt.GRAMMAR_EXAMPLES_INSERT_QUERY, grades);

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

  public String getVocabularyResponseForGrade(String grade) throws IOException {
    String jsonResponse =
        alanAIClient.sendRequestToAlanAI(AlanAIRequestPrompt.VOCABULARY_DEFAULT_PROMPT, grade);
    return extractContent(jsonResponse);
  }

  private String extractContent(String json) throws IOException {
    JsonNode rootNode = objectMapper.readTree(json);
    return rootNode.path("content").asText();
  }
}
