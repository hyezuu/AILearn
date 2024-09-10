package com.example.ormi5finalteam1.domain.post.dto;

import com.example.ormi5finalteam1.domain.comment.dto.AdminCommentDto;
import com.example.ormi5finalteam1.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminPostDetailDto {

    private String nickname;
    private String title;
    private String contents;
    private int viewCount;
    private List<AdminCommentDto> comments;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public static AdminPostDetailDto toDto (Post post, List<AdminCommentDto> comments) {
        return AdminPostDetailDto.builder()
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .contents(post.getContent())
                .viewCount(post.getViewCount())
                .comments(comments)
                .createdAt(post.getCreatedAt())
                .deletedAt(post.getDeletedAt())
                .build();
    }
}
