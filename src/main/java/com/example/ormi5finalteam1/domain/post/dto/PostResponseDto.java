package com.example.ormi5finalteam1.domain.post.dto;

import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private User user;
    private String title;
    private String content;
    private int viewCount;

    public PostResponseDto(Long id, User user, String title, String content, int viewCount) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
    }

}
