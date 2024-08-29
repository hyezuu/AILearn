package com.example.ormi5finalteam1.domain.essay.dto.response;

import com.example.ormi5finalteam1.domain.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EssayGuideResponseDto {
    private Grade grade;
    private String content;
}
