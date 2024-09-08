package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vocabulary-list")
public class VocabularyInfoController {

    @GetMapping
    public String getVocabularyInfo() {
        return "vocabulary-list/index";
    }

}
