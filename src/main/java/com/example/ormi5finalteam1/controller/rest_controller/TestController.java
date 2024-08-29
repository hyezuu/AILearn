package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.TestQuestionResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestController {

    private final TestService testService;

    @GetMapping("/tests")
    public List<TestQuestionResponseDto> getTests(@AuthenticationPrincipal Provider provider,
                                                  @RequestParam Grade grade) {
        return testService.getTests(provider.grade(), grade);
    }
}
