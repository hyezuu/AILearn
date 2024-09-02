package com.example.ormi5finalteam1.domain.grammar_example.dto;

import com.example.ormi5finalteam1.domain.Grade;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GrammarExampleDto {
  private Long id;
  private Grade grade;
  private String question;
  private String answer;
  private String commentary;
  private LocalDateTime createdAt;
  ;
}
