package com.example.ormi5finalteam1.controller.rest_controller;

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
        return testService.getLevelTests(selectedGrade);
    }

    @GetMapping("/upgrade-tests")
    public List<TestQuestionResponseDto> getUpgradeTests(@AuthenticationPrincipal Provider provider) {
        return testService.getUpgradeTests(provider);
    }

    @PostMapping("/grade")
    public boolean submitTests(@AuthenticationPrincipal Provider provider,
                               @RequestParam Grade grade,
                               @RequestBody SubmitRequestVo submitRequestVo) {
        return testService.submitTests(grade, submitRequestVo);
    }
}
