package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.service.GameService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameInfoController {

    private final GameService gameService;

    @GetMapping
    public String gameView(Model model, @AuthenticationPrincipal Provider provider) {
        int highScore = gameService.getHighScore(provider.id());
        List<Vocabulary> words = gameService.getRandomWords(10); // 10개의 랜덤 단어 가져오기

        model.addAttribute("highScore", highScore);
        model.addAttribute("words", words);
        return "vocabulary-list/game";
    }
}
