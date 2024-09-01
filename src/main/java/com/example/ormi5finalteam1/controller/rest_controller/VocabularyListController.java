package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.VocabularyListService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VocabularyListController {

    private final VocabularyListService vocabularyListService;

    @PostMapping("/vocabulary-list")
    public void create(@AuthenticationPrincipal Provider provider) {
        vocabularyListService.create(provider);
    }
}
