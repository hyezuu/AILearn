package com.example.ormi5finalteam1.domain.test;


import com.example.ormi5finalteam1.domain.Grade;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestQuestionResponseDto {

    private Grade grade;

    private Long testId;

    private String question;

    public static TestQuestionResponseDto toDto(Test test){
        return TestQuestionResponseDto.builder()
                .grade(test.getGrade())
                .testId(test.getId())
                .question(test.getQuestion())
                .build();
    }
}
