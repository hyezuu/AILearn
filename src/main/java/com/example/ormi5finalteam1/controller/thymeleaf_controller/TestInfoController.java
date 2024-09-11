package com.example.ormi5finalteam1.controller.thymeleaf_controller;

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
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class TestInfoController {

    private final TestService testService;
    private final UserService userService;

    @GetMapping("/tests")
    public String getSelectLevelPage(@AuthenticationPrincipal Provider provider) {
        User user = getUser(provider);
        if (user.getGrade() != null || !user.isReadyForUpgrade()) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        return "tests/select-level";
    }

    @PostMapping("/level-tests")
    public String selectTestLevel(@AuthenticationPrincipal Provider provider,
                                  @RequestParam("grade") Grade selectedGrade) {

        User user = getUser(provider);
        if (user.getGrade() != null || !user.isReadyForUpgrade()) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        return "redirect:/tests/level-tests?grade=" + selectedGrade;
    }

    @GetMapping("/tests/level-tests")
    public String showLevelTests(@AuthenticationPrincipal Provider provider,
                                 @RequestParam("grade") Grade selectedGrade, Model model) {

        User user = getUser(provider);
        if (user.getGrade() != null || !user.isReadyForUpgrade()) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        model.addAttribute("testQuestionResponseDtoList", testService.getLevelTests(selectedGrade));
        model.addAttribute("grade", selectedGrade);
        return "tests/level-test";
    }

    @PostMapping("/grade/A1")
    @ResponseStatus(HttpStatus.OK)
    public void setA1(@AuthenticationPrincipal Provider provider) {
        User user = getUser(provider);
        if (user.getGrade() != null || !user.isReadyForUpgrade()) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);
        testService.setA1(user);
    }

    @ResponseBody
    @PostMapping("/grade")
    public ResponseEntity<TestResultResponseDto> submitLevelTests(@AuthenticationPrincipal Provider provider,
                                                                  @RequestParam("grade") Grade grade,
                                                                  @RequestBody SubmitRequestVo submitRequestVo) {
        User user = getUser(provider);
        if (user.getGrade() != null) throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);

        return ResponseEntity.ok(testService.thymeleafSubmitLevelTests(user, grade, submitRequestVo));
    }

    @GetMapping("/test-result")
    public String showTestResult(@AuthenticationPrincipal Provider provider, Model model) {

        User user = getUser(provider);
        model.addAttribute("user", user);
        return "tests/test-result";
    }

    @GetMapping("/upgrade-tests")
    public String showUpgradeTests(@AuthenticationPrincipal Provider provider, Model model) {

        User user = getUser(provider);
        if (user.getGrade() == null || user.getGrade().equals(Grade.C2) || !user.isReadyForUpgrade())
            throw new BusinessException(ErrorCode.CANNOT_TAKE_TEST);

        List<TestQuestionResponseDto> questionList = testService.getUpgradeTests(user);
        model.addAttribute("testQuestionResponseDtoList", questionList);
        model.addAttribute("grade", questionList.get(0).getGrade());
        return "tests/upgrade-test";
    }

    private User getUser(@AuthenticationPrincipal Provider provider) {
        return userService.loadUserByUsername(provider.email());
    }

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {

        model.addAttribute("errorMessage", e.getMessage());
        return "tests/error";
    }

}
