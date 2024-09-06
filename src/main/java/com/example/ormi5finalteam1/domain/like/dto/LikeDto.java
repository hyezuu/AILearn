package com.example.ormi5finalteam1.domain.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private Long id;
    private Long userId;
    private Long postId;
    private LocalDateTime createdAt;
}