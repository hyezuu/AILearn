package com.example.ormi5finalteam1.external.api.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest {
  private String content;
  private String client_id;
}
