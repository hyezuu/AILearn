package com.example.ormi5finalteam1.domain.user.dto;

import com.example.ormi5finalteam1.domain.user.User;

public record GetTop5UserByScore(
    String nickname,
    int highScore
) {
    public static GetTop5UserByScore from(User user) {
        return new GetTop5UserByScore(user.getNickname(), user.getHighScore());
    }
}
