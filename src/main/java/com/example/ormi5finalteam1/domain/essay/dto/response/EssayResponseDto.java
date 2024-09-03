package com.example.ormi5finalteam1.domain.essay.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class EssayResponseDto {
    private String topic;
    private String content;
    private LocalDateTime createdAt;
}
