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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
     * A2: 10문항
     * B1 ~ B2: 15문항
     * C1 ~ C2: 20문항
     *
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
     * A1~A2: 10문항 -> 60점(6문제) 이상 통과 (한 문제 당 10점)
     * B1~B2: 15문항 -> 70점(10문제) 이상 통과 (한 문제 당 7점)
     * C1: 20문항 -> 80점(16문제) 이상 통과 (한 문제 당 5점)
     *
     * @param grade           사용자가 지정한 등급
     * @param submitRequestVo 사용자가 제출한 문제, 답안이 들어있는 Vo
     * @return 설정된 사용자의 등급
     */
    @Transactional
    public Grade submitLevelTests(Provider provider, Grade grade, SubmitRequestVo submitRequestVo) {

        User user = userService.loadUserByUsername(provider.email());
        if (!user.isReadyForUpgrade()) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);

        int count = markAnswer(submitRequestVo);

        if ((grade.equals(Grade.A2) && count >= 6) ||
                ((grade.equals(Grade.B1) || grade.equals(Grade.B2)) && count >= 10) ||
                (grade.equals(Grade.C1) && count >= 16))
            user.changeGrade(grade);
        return user.getGrade();
    }

    /**
     * 승급 테스트 시 정답을 채점
     * 통과 기준은 상기와 같음
     * A1(A레벨 승급): 10문항 -> 20점 이하(2문제) 강등
     * A2 ~ B1(B레벨 승급): 15문항 -> 30점 이하(4문제) 강등
     * B2 ~ C1(C레벨 승급): 20문항 -> 35점 이하(7문제) 강등
     *
     * @param provider        현재 승급시험을 볼 사용자
     * @param submitRequestVo 사용자가 제출한 문제, 답안이 들어있는 Vo
     * @return 테스트에 통과했으면 true, 아니면 false
     */
    @Transactional
    public Grade submitUpgradeTests(Provider provider, SubmitRequestVo submitRequestVo) {

        User user = userService.loadUserByUsername(provider.email());
        if (!user.isReadyForUpgrade()) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);

        // TODO 유저의 다음 등급과 문제의 등급이 일치하는지 확인?
        int count = markAnswer(submitRequestVo);

        Grade[] values = Grade.values();
        int nowGradeIndex = user.getGrade().getIndex();
        Grade nextGrade = values[nowGradeIndex + 1];

        // 승급
        if ((nextGrade.equals(Grade.A2) && count >= 6) ||
                ((nextGrade.equals(Grade.B1) || nextGrade.equals(Grade.B2)) && count >= 10) ||
                (nextGrade.equals(Grade.C1) && count >= 16))
            user.changeGrade(nextGrade);
        // 강등
        else if ((nextGrade.equals(Grade.A2) && count <= 2) ||
                ((nextGrade.equals(Grade.B1) || nextGrade.equals(Grade.B2)) && count <= 4) ||
                ((nextGrade.equals(Grade.C1) || nextGrade.equals(Grade.C2)) && count <= 7))
            user.changeGrade(values[nowGradeIndex - 1]);

        return user.getGrade();
    }

    private int markAnswer(SubmitRequestVo submitRequestVo) {
        
        int count = 0;
        
        for (SubmitRequestDto requestDto : submitRequestVo.getDtoList()) {
            Test test = testRepository.findById(requestDto.getTestId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.TEST_NOT_FOUND));

            if (requestDto.getAnswer().equals(test.getAnswer())) count++;
        }

        return count;
    }
}
