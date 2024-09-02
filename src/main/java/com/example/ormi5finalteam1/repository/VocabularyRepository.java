package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    Boolean existsByWord(String word);
    List<Vocabulary> findTop10ByGradeAndIdGreaterThanOrderById(Grade grade, Long id);
}
