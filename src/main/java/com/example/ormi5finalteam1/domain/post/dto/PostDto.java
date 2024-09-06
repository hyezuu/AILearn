package com.example.ormi5finalteam1.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private Long userId;
    private String userNickname;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int viewCount;
}
