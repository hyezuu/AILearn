package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/posts")
public class PostInfoController {
    @GetMapping
    public String GetPostInfo() {
        return "posts/index";
    }
    @GetMapping("/new")
    public String GetPostNew() {
        return "posts/new";
    }
    @GetMapping("/{id}/edit")
    public String GetPostEdit() {
        return "posts/edit";
    }
    @GetMapping("/{id}")
    public String GetPostDetail() {
        return "posts/detail";
    }
}
