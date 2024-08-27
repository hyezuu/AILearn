package com.example.ormi5finalteam1.domain.grammar_examples;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class GrammarExamples {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
