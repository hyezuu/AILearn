package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class grammerExamplesInfoController {

    @GetMapping("/grammar-examples")
    public String getGrammerExamplesInfo() {
        return "grammar-examples/index";
    }
}
