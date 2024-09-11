package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.SubmitRequestVo;
import com.example.ormi5finalteam1.domain.test.TestQuestionResponseDto;
import com.example.ormi5finalteam1.domain.test.TestResultResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.service.TestService;
import com.example.ormi5finalteam1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestController {

    private final TestService testService;
    private final UserService userservice;

    @GetMapping("/level-tests")
    public List<TestQuestionResponseDto> getLevelTests(@AuthenticationPrincipal Provider provider,
                                                  @RequestParam("grade") Grade selectedGrade) {

        if (provider.grade() != null) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        return testService.getLevelTests(selectedGrade);
    }

    @GetMapping("/upgrade-tests")
    public List<TestQuestionResponseDto> getUpgradeTests(@AuthenticationPrincipal Provider provider) {
        if (provider.grade() == null) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        return testService.getUpgradeTests(userservice.loadUserByUsername(provider.email()));
    }

    @PostMapping("/grade")
    public Grade submitLevelTests(@AuthenticationPrincipal Provider provider,
                                    @RequestParam Grade grade,
                                    @RequestBody SubmitRequestVo submitRequestVo) {

        if (provider.grade() != null) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        return testService.submitLevelTests(provider, grade, submitRequestVo);
    }
    @PostMapping("/upgrade")
    public ResponseEntity<TestResultResponseDto> submitUpgradeTests(@AuthenticationPrincipal Provider provider,
                                                                    @RequestBody SubmitRequestVo submitRequestVo) {

        User user = userservice.loadUserByUsername(provider.email());
        if (user.getGrade() == null || !user.isReadyForUpgrade()) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        return ResponseEntity.ok(testService.renewalSubmitUpgradeTests(user, submitRequestVo));
    }

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {

        model.addAttribute("errorMessage", e.getMessage());
        return "tests/error";
    }
}
