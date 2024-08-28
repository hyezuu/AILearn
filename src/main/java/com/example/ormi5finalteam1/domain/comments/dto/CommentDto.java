package com.example.ormi5finalteam1.domain.comments.dto;

import com.example.ormi5finalteam1.domain.comments.Comment;
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
public class CommentDto {
    private Long id;
    private User user;
    private Post post;
    private String content;
    private LocalDateTime createdAt;

    public static CommentDto fromEntity(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .post(comment.getPost())
                .user(comment.getUser())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public Comment toEntity(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getUser(),
                commentDto.getPost(),
                commentDto.getContent(),
                commentDto.getCreatedAt()
        );
    }
}