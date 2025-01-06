package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import com.example.ormi5finalteam1.domain.vocabulary.dto.MyVocabularyListResponseDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VocabularyListRepository extends JpaRepository<VocabularyList, Long> {

    Optional<VocabularyList> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);

    @Query("SELECT vl FROM VocabularyList vl " +
        "JOIN FETCH vl.vocabularyEntries v " +
        "WHERE vl.user.id = :userId " +
        "AND vl.deletedAt IS NULL " +
        "ORDER BY vl.createdAt DESC")
    Page<VocabularyList> findVocabularyListByUserId(@Param("userId") Long userId, Pageable pageable);

    Page<VocabularyList> findByUserIdAndVocabularyEntries_Vocabulary_DeletedAtIsNullOrderByCreatedAtDesc(
        Long userId, Pageable pageable
    );
}
