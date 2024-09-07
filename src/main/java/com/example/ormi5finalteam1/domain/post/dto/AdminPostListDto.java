package com.example.ormi5finalteam1.domain.post.dto;

import com.example.ormi5finalteam1.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminPostListDto {

    private Long id;
    private String nickname;
    private String title;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public static AdminPostListDto toDto(Post post) {
        return AdminPostListDto.builder()
                .id(post.getId())
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .deletedAt(post.getDeletedAt())
                .build();
    }
}
