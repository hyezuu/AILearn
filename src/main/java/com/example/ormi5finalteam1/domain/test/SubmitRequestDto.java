package com.example.ormi5finalteam1.domain.test;

import lombok.Getter;


@Getter
public class SubmitRequestDto {
    private final Long testId;
    private final String answer;

    public SubmitRequestDto(long i, String s) {
        this.testId = i;
        this.answer = s;
    }
}
