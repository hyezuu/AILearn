package com.example.ormi5finalteam1.external.api.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {
  private String content;

  @Override
  public String toString() {
    return "BaseResponse{" + "content='" + content + '\'' + '}';
  }
}
