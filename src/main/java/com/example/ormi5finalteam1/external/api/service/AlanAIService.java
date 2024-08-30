package com.example.ormi5finalteam1.external.api.service;

import com.example.ormi5finalteam1.external.api.client.AlanAIClient;
import com.example.ormi5finalteam1.external.api.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlanAIService {
  private final AlanAIClient alanAIClient;

//  public BaseResponse getGrammarExamplesQuery(String prompt) {
//    alanAIClient.sendRequestToOpenAI(prompt);
//  }
}
