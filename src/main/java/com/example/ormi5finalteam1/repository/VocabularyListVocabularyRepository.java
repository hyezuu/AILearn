package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyListVocabulary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VocabularyListVocabularyRepository extends JpaRepository<VocabularyListVocabulary, Long> {
    @Query("SELECT MAX(vlv.vocabulary.id) FROM VocabularyListVocabulary vlv " +
        "WHERE vlv.vocabularyList.id = :vocabularyListId AND vlv.grade = :grade")
    Optional<Long> findMaxVocabularyIdByVocabularyListIdAndGrade(
        @Param("vocabularyListId") Long vocabularyListId,
        @Param("grade") Grade grade);
}
