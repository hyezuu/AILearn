package com.example.ormi5finalteam1.domain.comments.dto;

import com.example.ormi5finalteam1.domain.comments.Comments;
import com.example.ormi5finalteam1.domain.posts.Post;
import com.example.ormi5finalteam1.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsDto {
    private Long id;
    private User userId;
    private Post postId;
    private String content;
    private LocalDateTime createdAt;

    public static CommentsDto fromEntity(Comments comments) {
        return CommentsDto.builder()
                .id(comments.getId())
                .postId(comments.getPostId())
                .userId(comments.getUserId())
                .content(comments.getContent())
                .createdAt(comments.getCreatedAt())
                .build();
    }

    public static Comments toEntity(CommentsDto commentDto) {
        return new Comments(
                commentDto.getId(),
                commentDto.getUserId(),
                commentDto.getPostId(),
                commentDto.getContent(),
                commentDto.getCreatedAt()
        );
    }

}