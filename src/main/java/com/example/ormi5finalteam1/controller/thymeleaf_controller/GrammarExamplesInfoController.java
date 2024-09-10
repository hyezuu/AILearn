package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GrammarExamplesInfoController {

    @GetMapping("/grammar-examples")
    public String getGrammarExamplesInfo() {
        return "grammar-examples/index";
    }
}
