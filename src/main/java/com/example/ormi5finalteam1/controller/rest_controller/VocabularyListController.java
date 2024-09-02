package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.VocabularyListService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vocabulary-list")
@RequiredArgsConstructor
public class VocabularyListController {

    private final VocabularyListService vocabularyListService;

    @PostMapping
    public void create(@AuthenticationPrincipal Provider provider) {
        vocabularyListService.create(provider);
    }

    @PostMapping("/me/vocabularies")
    public void addVocabularies(@AuthenticationPrincipal Provider provider) {
        vocabularyListService.addVocabulary(provider);
    }
}
