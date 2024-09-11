package com.example.ormi5finalteam1.domain.test;

import com.example.ormi5finalteam1.domain.Grade;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestResultResponseDto {
    private String status;
    private Grade grade;
    private int score;

    public static TestResultResponseDto toDto(String status, Grade grade, int score) {
        return TestResultResponseDto.builder()
                .status(status)
                .grade(grade)
                .score(score)
                .build();
    }
}
