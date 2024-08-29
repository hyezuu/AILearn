package com.example.ormi5finalteam1.common.response;

import lombok.*;

@Data
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SingleResponseDto<T> {
    private T data;
}
