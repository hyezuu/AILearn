package com.example.ormi5finalteam1.common.response;

import lombok.Data;

@Data
public class BaseResponseDto {
  private String statusCode;
  private String statusMessage;
}
