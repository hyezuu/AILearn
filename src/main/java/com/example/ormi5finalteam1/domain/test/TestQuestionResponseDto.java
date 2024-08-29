package com.example.ormi5finalteam1.domain.test;


import com.example.ormi5finalteam1.domain.Grade;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestQuestionResponseDto {

    private Grade grade;

    private String question;

    public static TestQuestionResponseDto toDto(Test test){
        return TestQuestionResponseDto.builder()
                .grade(test.getGrade())
                .question(test.getQuestion())
                .build();
    }
}
