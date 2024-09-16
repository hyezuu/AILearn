package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.dto.GetTop5UserByScore;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.dto.MyScoreDto;
import com.example.ormi5finalteam1.service.GameService;
import com.example.ormi5finalteam1.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final UserService userService;

    @PostMapping("/score")
    public void saveScore(@AuthenticationPrincipal Provider provider, @RequestBody
    MyScoreDto myScoreDto) {
        gameService.saveScore(provider.id(), myScoreDto.score());
    }

    @GetMapping("/words")
    public List<Vocabulary> getWords(@RequestParam(defaultValue = "10") int count) {
        return gameService.getRandomWords(count);
    }

    @GetMapping("/rankings")
    public List<GetTop5UserByScore> getTop5Rankings() {
        return userService.getTop5UserByScore();
    }
}
