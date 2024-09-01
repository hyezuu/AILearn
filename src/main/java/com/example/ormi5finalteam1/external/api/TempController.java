package com.example.ormi5finalteam1.external.api;

import com.example.ormi5finalteam1.external.api.service.AlanAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alan")
public class TempController {

  private final AlanAIService alanAIService;

  @GetMapping("/grammar-examples")
  public void getGrammarExamples(@RequestParam String grade) {
    alanAIService.getGrammarExamplesQuery(grade);
  }
}
