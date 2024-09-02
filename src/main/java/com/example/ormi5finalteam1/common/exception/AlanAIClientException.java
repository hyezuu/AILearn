package com.example.ormi5finalteam1.common.exception;

public class AlanAIClientException extends RuntimeException {
  public AlanAIClientException(String message) {
    super(message);
  }

  public AlanAIClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
