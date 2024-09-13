package com.example.ormi5finalteam1.external.api.client;

import com.example.ormi5finalteam1.common.exception.AlanAIClientException;
import com.example.ormi5finalteam1.external.api.dto.BaseRequest;
import com.example.ormi5finalteam1.external.api.dto.BaseResponse;
import com.example.ormi5finalteam1.external.constants.AlanAIRequestPrompt;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlanAIClient {

  private final RestTemplate restTemplate;

  @Value("${alanai.api.question_url}")
  private String alanAiApiUrl;

  @Value("${alanai.api.key}")
  private String alanAiApiKey;

  public BaseResponse sendRequestToAlanAI(AlanAIRequestPrompt prompt, String... variables) {
    try {
      String content = prompt.getPromptTemplate();

      // HTTP 헤더 설정
      HttpHeaders headers = new HttpHeaders();
      headers.set("Content-Type", "application/json");

      // BaseRequest 객체 생성
      if (variables != null) {
        content = String.format(prompt.getPromptTemplate(), variables[0]); // todo: 로직 수정 필요
      }

      BaseRequest request = new BaseRequest(content, alanAiApiKey);

      // URI 생성 - client_id를 쿼리 파라미터로 추가
      URI uri =
          UriComponentsBuilder.fromHttpUrl(alanAiApiUrl)
              .queryParam("client_id", request.getClient_id())
              .queryParam("content", request.getContent())
              .encode()
              .build()
              .toUri();

      log.info("Calling API: {}", uri);

      // GET 요청 보내고 응답 받기(getForObject 방식)
      BaseResponse response = restTemplate.getForObject(uri, BaseResponse.class);

      log.info("API response received");
      log.info("Response: {}", response);
      return response;

    } catch (HttpClientErrorException e) {
      log.info(e.getResponseBodyAsString());
      // 오류 처리
      throw new AlanAIClientException("AlanAI API 요청 실패: " + e.getMessage(), e);
    }
  }

  public String sendRequestToAlanAI(AlanAIRequestPrompt prompt, String grade) {
    try {
      String content = String.format(prompt.getPromptTemplate(), grade);
      String uri = String.format("%s?content=%s&client_id=%s", alanAiApiUrl, content, alanAiApiKey);
      log.info("Calling API: {}", uri);
      String response = restTemplate.getForObject(uri, String.class);
      log.info("API response received");
      log.info("Response: {}", response);
      return response;
    } catch (HttpClientErrorException e) {
      throw new AlanAIClientException("AlanAI API 요청 실패: " + e.getMessage(), e);
    }
  }
}
