package com.example.ormi5finalteam1.domain.post.dto;

import com.example.ormi5finalteam1.domain.user.User;
import lombok.Getter;

@Getter
public class PostRequestDto {

    private User user;
    private String title;
    private String content;

    public PostRequestDto(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }
}
