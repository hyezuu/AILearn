package com.example.ormi5finalteam1.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostReponseDto {
    private Long id;
    private String userNickname;
    private String title;
    private LocalDateTime createAt;
    private int viewCount;
}
