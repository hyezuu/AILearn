package com.example.ormi5finalteam1.domain.user.dto;

import com.example.ormi5finalteam1.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserInfoDto {

    private Long userId;
    private String email;
    private String nickname;
    private boolean isActive;
    private int level;
    private boolean isReadyForUpgrade;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginedAt;
    private LocalDateTime deletedAt;

    public static UserInfoDto toDto(User user) {
        return UserInfoDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .isActive(user.isActive())
                .level(user.getLevel())
                .isReadyForUpgrade(user.isReadyForUpgrade())
                .createdAt(user.getCreatedAt())
                .lastLoginedAt(user.getLastLoginedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}
