package com.example.ormi5finalteam1.domain.essay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record EssayRequestDto (
        @NotNull Long userId,
        @NotBlank @Length(max = 100) String topic,
        @NotBlank String content
) {
}
