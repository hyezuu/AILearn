package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/essays")
public class EssayInfoController {
@GetMapping("/new")
    public String getEssayNew(Model model) {
    return "essays/new";
}
    @GetMapping
    public String getEssayInfo(Model model) {
        return "essays/index";
    }
    @GetMapping("/{id}")
    public String getEssayDetail(Model model) {
        return "essays/detail";
    }
    @GetMapping("/{id}/edit")
    public String getEssayEdit(Model model) {
        return "essays/edit";
    }
    @GetMapping("/{id}/review")
    public String getEssayReview(Model model) {
        return "essays/review";
    }
    @GetMapping("/guide")
    public String getEssayGuide(Model model) {
        return "essays/guide";
    }
}
