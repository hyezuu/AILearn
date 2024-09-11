package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabularyListRepository extends JpaRepository<VocabularyList, Long> {

    Optional<VocabularyList> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);
}
