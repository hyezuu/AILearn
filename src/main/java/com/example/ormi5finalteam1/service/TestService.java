package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.SubmitRequestDto;
import com.example.ormi5finalteam1.domain.test.SubmitRequestVo;
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

    /**
     * 최초 레벨 테스트 시 정답을 채점
     * A2: 10문항 -> 60점 이상 통과 (한 문제 당 10점, 6문제 이상 통과)
     * B1~B2: 15문항 -> 70점 이상 통과 (한 문제 당 7점, 10문제 이상 통과)
     * C1: 20문항 -> 80점 이상 통과 (한 문제 당 5점, 16문제 이상 통과)
     * @param grade 사용자가 지정한 등급
     * @param submitRequestVo 사용자가 제출한 문제, 답안이 들어있는 Vo
     * @return 테스트에 통과했으면 true, 아니면 false
     */
    public boolean submitTests(Grade grade, SubmitRequestVo submitRequestVo) {

        int count = 0;
        for (SubmitRequestDto requestDto : submitRequestVo.getDtoList()) {
            Test test = testRepository.findById(requestDto.getTestId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.TEST_NOT_FOUND));

            if(requestDto.getAnswer().equals(test.getAnswer())) count++;
        }

        return (grade.equals(Grade.A2) && count >= 6) ||
                ((grade.equals(Grade.B1) || grade.equals(Grade.B2)) && count >= 10) ||
                (grade.equals(Grade.C1) && count >= 16);
    }
}
