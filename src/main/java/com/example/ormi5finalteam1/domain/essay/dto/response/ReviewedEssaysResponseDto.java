package com.example.ormi5finalteam1.domain.essay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewedEssaysResponseDto {
    private String essayContent;
    private String reviewedContent;
}
