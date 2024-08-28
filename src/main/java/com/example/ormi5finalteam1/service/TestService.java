package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.Test;
import com.example.ormi5finalteam1.domain.test.TestQuestionResponseDto;
import com.example.ormi5finalteam1.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;

    public List<TestQuestionResponseDto> getTests(Grade userGrade, Grade grade) {

        List<Test> gradeTestQuestions = testRepository.findByGrade(grade == null? userGrade : grade);

        return gradeTestQuestions.stream()
                .map(TestQuestionResponseDto::toDto)
                .collect(Collectors.toList());
    }
}
