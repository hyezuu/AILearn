package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyListVocabulary;
import com.example.ormi5finalteam1.domain.vocabulary.dto.MyVocabularyListResponseDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VocabularyListVocabularyRepository extends JpaRepository<VocabularyListVocabulary, Long> {
    @Query("SELECT MAX(vlv.vocabulary.id) FROM VocabularyListVocabulary vlv " +
        "WHERE vlv.vocabularyList.id = :vocabularyListId AND vlv.grade = :grade")
    Optional<Long> findMaxVocabularyIdByVocabularyListIdAndGrade(
        @Param("vocabularyListId") Long vocabularyListId,
        @Param("grade") Grade grade);

    @Query("SELECT vlv FROM VocabularyListVocabulary vlv " +
        "WHERE vlv.id = :id AND vlv.vocabularyList.user.id = :userId AND vlv.deletedAt IS NULL")
    Optional<VocabularyListVocabulary> findByIdAndVocabularyListUserId(Long userId, Long id);

    @Query("SELECT vlv FROM VocabularyListVocabulary vlv " +
        "JOIN FETCH vlv.vocabulary v " +
        "JOIN FETCH vlv.vocabularyList vl " +
        "WHERE vlv.vocabularyList.user.id = :userId " +
        "AND vlv.deletedAt IS NULL " +
        "ORDER BY vlv.createdAt DESC")
    Page<VocabularyListVocabulary> findByUserIdOrderByCreatedAtDesc(@Param("userId") long id,
        Pageable pageable);

    // N+1 문제가 발생하는 기본 메서드
    Page<VocabularyListVocabulary> findByVocabularyListUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(
        Long userId, Pageable pageable
    );

    @Query(value = "SELECT new com.example.ormi5finalteam1.domain.vocabulary.dto.MyVocabularyListResponseDto(" +
        "vlv.id, v.word, v.meaning, v.exampleSentence, " +
        "CAST(vlv.grade AS string), vlv.createdAt) " +
        "FROM VocabularyListVocabulary vlv " +
        "JOIN vlv.vocabulary v " +
        "WHERE vlv.vocabularyList.user.id = :userId " +
        "AND vlv.deletedAt IS NULL " +
        "ORDER BY vlv.createdAt DESC",
        countQuery = "SELECT COUNT(vlv) " +
            "FROM VocabularyListVocabulary vlv " +
            "WHERE vlv.vocabularyList.user.id = :userId " +
            "AND vlv.deletedAt IS NULL")
    Page<MyVocabularyListResponseDto> findMyVocabularyList(@Param("userId") long id, Pageable pageable);
}
