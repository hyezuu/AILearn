package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.SubmitRequestVo;
import com.example.ormi5finalteam1.domain.test.TestQuestionResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestController {

    private final TestService testService;

    @GetMapping("/level-tests")
    public List<TestQuestionResponseDto> getLevelTests(@AuthenticationPrincipal Provider provider,
                                                  @RequestParam("grade") Grade selectedGrade) {

        if (provider.grade() != null) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        return testService.getLevelTests(selectedGrade);
    }

    @GetMapping("/upgrade-tests")
    public List<TestQuestionResponseDto> getUpgradeTests(@AuthenticationPrincipal Provider provider) {
        if (provider.grade() == null) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        return testService.getUpgradeTests(provider);
    }

    @PostMapping("/grade")
    public Grade submitLevelTests(@AuthenticationPrincipal Provider provider,
                                    @RequestParam Grade grade,
                                    @RequestBody SubmitRequestVo submitRequestVo) {

        if (provider.grade() != null) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        return testService.submitLevelTests(provider, grade, submitRequestVo);
    }
    @PostMapping("/upgrade")
    public Grade submitUpgradeTests(@AuthenticationPrincipal Provider provider,
                                    @RequestBody SubmitRequestVo submitRequestVo) {

        if (provider.grade() == null) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);

        return testService.submitUpgradeTests(provider, submitRequestVo);
    }
}
