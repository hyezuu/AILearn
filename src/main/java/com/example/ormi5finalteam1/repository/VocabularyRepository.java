package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    Boolean existsByWord(String word);
    List<Vocabulary> findTop10ByGradeAndIdGreaterThanOrderById(Grade grade, Long id);
    @Query("SELECT v FROM Vocabulary v ORDER BY FUNCTION('RAND')")
    List<Vocabulary> findRandomWords(Pageable pageable);
}
