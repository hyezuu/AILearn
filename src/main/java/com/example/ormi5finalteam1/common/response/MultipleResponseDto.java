package com.example.ormi5finalteam1.common.response;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultipleResponseDto<T> {
  private List<T> data;
  private int totalCount;
}
