package com.example.ormi5finalteam1.domain.grammar_example.dto;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.grammar_example.GrammarExample;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GrammarExampleDto extends GrammarExample {
  public GrammarExampleDto(Grade grade, String question, String answer, String commentary) {
    super(grade, question, answer, commentary);
  }

  @JsonIgnore // JSON 직렬화에서 제외할 필드
  private LocalDateTime updatedAt;

  @JsonIgnore // JSON 직렬화에서 제외할 필드
  private LocalDateTime deletedAt;

  // Getters and Setters
  // 부모 클래스의 Getters/Setters 중에서 제외할 필드만 Override하여 @JsonIgnore 사용

  @Override
  @JsonIgnore
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  @Override
  @JsonIgnore
  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }
}
