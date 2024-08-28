package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping("/email-duplication")
    public boolean checkEmail(String email) {
        return userService.isDuplicateEmail(email);
    }

    @GetMapping("/nickname-duplication")
    public boolean checkNickname(String nickname) {
        return userService.isDuplicateNickname(nickname);
    }

    @PostMapping("/signup")
    public void signup(@RequestBody CreateUserRequestDto requestDto) {
        userService.createUser(requestDto);
    }

    @GetMapping("/me")
    public Provider getMe(@AuthenticationPrincipal Provider provider){
        return provider;
    }
}
