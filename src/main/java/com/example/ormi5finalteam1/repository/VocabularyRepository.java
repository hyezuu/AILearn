package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    Boolean existsByWord(String word);
}
