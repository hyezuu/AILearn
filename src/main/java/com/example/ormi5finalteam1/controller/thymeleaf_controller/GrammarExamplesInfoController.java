package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class GrammarExamplesInfoController {

    private final UserService userService;

    @GetMapping("/grammar-examples")
    public String getGrammarExamplesInfo(@AuthenticationPrincipal Provider provider, Model model) {
        User user = userService.getUser(provider.id());
        model.addAttribute("hasGrade", user.getGrade()!=null);
        return "grammar-examples/index";
    }
}
