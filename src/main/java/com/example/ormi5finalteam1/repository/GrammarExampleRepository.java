package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.grammar_example.GrammarExample;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrammarExampleRepository extends JpaRepository<GrammarExample, Long> {
  Page<GrammarExample> findByQuestionContainingAndGrade(
      String question, Grade grade, Pageable pageable);

  Page<GrammarExample> findByGrade(Grade grade, Pageable pageable);
}
