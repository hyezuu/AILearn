package com.example.ormi5finalteam1.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminCommentDto {

    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;

    public static AdminCommentDto toDto(CommentDto comment){
        return AdminCommentDto.builder()
                .id(comment.getId())
                .nickname(comment.getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
