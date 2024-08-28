package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("createUserRequestDto",
            new CreateUserRequestDto(null, null, null));
        return "user/signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute CreateUserRequestDto requestDto,
        BindingResult result) {
        userService.createUser(requestDto);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }
}
