package com.example.ormi5finalteam1.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long userId;
    private Long postId;
    private String content;
}