package com.example.ormi5finalteam1.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class EssayAlanApiService {

    private final RestClient restClient;

    public EssayAlanApiService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://kdt-api-function.azurewebsites.net")
                .build();
    }

    public String getApiResponse(String content, String clientId) {
        // GET 요청을 보내고 응답을 받아옴
        ResponseEntity<String> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/question/sse-streaming")
                        .queryParam("content", content)
                        .queryParam("client_id", clientId)
                        .build())
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("API 요청 실패: " + response.getStatusCode());
        }
    }
}
