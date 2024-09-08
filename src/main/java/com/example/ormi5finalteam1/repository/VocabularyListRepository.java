package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyListVocabulary;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VocabularyListRepository extends JpaRepository<VocabularyList, Long> {

    Optional<VocabularyList> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);

    @Query(value =
        "SELECT vlv FROM VocabularyListVocabulary vlv " +
        "JOIN FETCH vlv.vocabulary v " +
        "WHERE vlv.vocabularyList.user.id = :userId AND vlv.deletedAt IS NULL " +
        "ORDER BY vlv.createdAt DESC",
        countQuery =
            "SELECT COUNT(vlv) FROM VocabularyListVocabulary vlv " +
            "WHERE vlv.vocabularyList.user.id = :userId and vlv.deletedAt IS null")
    Page<VocabularyListVocabulary> findByUserIdOrderByCreatedAtDesc(@Param("userId") long id,
        Pageable pageable);
}
