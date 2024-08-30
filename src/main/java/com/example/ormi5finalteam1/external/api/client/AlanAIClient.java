package com.example.ormi5finalteam1.external.api.client;

import com.example.ormi5finalteam1.common.exception.AlanAIClientException;
import com.example.ormi5finalteam1.external.api.dto.BaseRequest;
import com.example.ormi5finalteam1.external.api.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class AlanAIClient {
  private final RestTemplate restTemplate;

  @Value("${alanai.api.question_url}")
  private String alanAiApiUrl;

  @Value("${alanai.api.question_url}")
  private String alanAiApiKey;

  public BaseResponse sendRequestToOpenAI(String prompt) {
    try {
      // HTTP 헤더 설정
      HttpHeaders headers = new HttpHeaders();
      headers.set("Content-Type", "application/json");

      // BaseRequest 객체 생성
      BaseRequest request = new BaseRequest(prompt, alanAiApiKey);

      // URI 생성 - client_id를 쿼리 파라미터로 추가
      URI uri = UriComponentsBuilder.fromHttpUrl(alanAiApiUrl)
              .queryParam("client_id", request.getClient_id())
              .queryParam("content", request.getContent())
              .build()
              .toUri();

      // GET 요청 보내고 응답 받기(getForObject 방식)
      BaseResponse response = restTemplate.getForObject(uri, BaseResponse.class);

      return response;

      // GET 요청 보내고 응답 받기(getForEntity 방식)
//      ResponseEntity<BaseResponse> responseEntity = restTemplate.getForEntity(uri, BaseResponse.class);
//
//      return responseEntity.getBody();

    } catch (HttpClientErrorException e) {
      // 오류 처리
      throw new AlanAIClientException("AlanAI API 요청 실패: " + e.getMessage(), e);
    }
  }
}
