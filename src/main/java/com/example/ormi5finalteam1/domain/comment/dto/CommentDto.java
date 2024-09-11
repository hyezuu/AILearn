package com.example.ormi5finalteam1.domain.comment.dto;

import com.example.ormi5finalteam1.domain.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long userId;
    private String nickname;
    private Grade userGrade;
    private Long postId;
    private String postTitle;
    private String content;
    private LocalDateTime createdAt;
}