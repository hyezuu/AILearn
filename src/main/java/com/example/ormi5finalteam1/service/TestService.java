package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.SubmitRequestDto;
import com.example.ormi5finalteam1.domain.test.SubmitRequestVo;
import com.example.ormi5finalteam1.domain.test.Test;
import com.example.ormi5finalteam1.domain.test.TestQuestionResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final UserService userService;

    /**
     * 레벨 테스트 시 문제 조회
     * A1은 시험 안 봄
     * A2 ~ C1은 시험 존재
     *
     * @param selectedGrade 사용자가 선택한 등급
     * @return 해당 레벨에 맞는 문제 목록
     */
    public List<TestQuestionResponseDto> getLevelTests(Grade selectedGrade) {

        if (selectedGrade.equals(Grade.A1) || selectedGrade.equals(Grade.C2))
            throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);

        List<Test> gradeTestQuestions = getTests(selectedGrade);

        return gradeTestQuestions.stream()
                .map(TestQuestionResponseDto::toDto)
                .collect(Collectors.toList());
    }


    /**
     * 승급 테스트 시 문제 조회
     * 승급 테스트 자격이 갖추어졌을 때만 응시 가능
     * 현재 회원이 A2이면 -> B1 문제, B2이면 -> C1 문제 출제
     * A2: 00점 이상 -> 승급, 00 ~ 00: 유지, 00점 이하: 강등
     * B1, B2:
     * C1, C2:
     * @param provider 현재 회원
     * @return 해당 레벨에 맞는 문제 리스트
     */
    public List<TestQuestionResponseDto> getUpgradeTests(Provider provider) {
        User user = userService.loadUserByUsername(provider.email());
        if (!user.isReadyForUpgrade()) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);

        Grade nextGrade = Grade.values()[provider.grade().getIndex() + 1];

        List<Test> upgradeTestQuestions = getTests(nextGrade);
        return upgradeTestQuestions.stream()
                .map(TestQuestionResponseDto::toDto)
                .collect(Collectors.toList());
    }

    // testRepository에서 grade에 맞는 test들을 뽑아 반환해주는 메서드
    private List<Test> getTests(Grade grade) {
        List<Test> questions = testRepository.findByGrade(grade);
        Collections.shuffle(questions);
        int size;
        if (grade.equals(Grade.A1) || grade.equals(Grade.A2)) size = 10;
        else if (grade.equals(Grade.B1) || grade.equals(Grade.B2)) size = 15;
        else size = 20;

        List<Test> gradeTestQuestions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            gradeTestQuestions.add(questions.get(i));
        }
        return gradeTestQuestions;
    }

    /**
     * 최초 레벨 테스트 시 정답을 채점
     * A2: 10문항 -> 60점 이상 통과 (한 문제 당 10점, 6문제 이상 통과)
     * B1~B2: 15문항 -> 70점 이상 통과 (한 문제 당 7점, 10문제 이상 통과)
     * C1: 20문항 -> 80점 이상 통과 (한 문제 당 5점, 16문제 이상 통과)
     *
     * @param grade           사용자가 지정한 등급
     * @param submitRequestVo 사용자가 제출한 문제, 답안이 들어있는 Vo
     * @return 테스트에 통과했으면 true, 아니면 false
     */
    public boolean submitTests(Grade grade, SubmitRequestVo submitRequestVo) {

        int count = 0;
        for (SubmitRequestDto requestDto : submitRequestVo.getDtoList()) {
            Test test = testRepository.findById(requestDto.getTestId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.TEST_NOT_FOUND));

            if (requestDto.getAnswer().equals(test.getAnswer())) count++;
        }

        return (grade.equals(Grade.A2) && count >= 6) ||
                ((grade.equals(Grade.B1) || grade.equals(Grade.B2)) && count >= 10) ||
                (grade.equals(Grade.C1) && count >= 16);
    }
}
