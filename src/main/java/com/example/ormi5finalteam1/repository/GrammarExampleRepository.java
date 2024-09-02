package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.grammar_example.GrammarExample;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrammarExampleRepository extends JpaRepository<GrammarExample, Long>, GrammarExampleCustomRepository {
  Page<GrammarExample> findByQuestionContainingAndGradeOrderByIdAsc(
      String question, Grade grade, Pageable pageable);

  Page<GrammarExample> findByGradeOrderByIdAsc(Grade grade, Pageable pageable);

  Optional<GrammarExample> findById(Long id);
}
