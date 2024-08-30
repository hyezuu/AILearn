package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.essay.ReviewedEssays;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewedEssaysRepository extends JpaRepository<ReviewedEssays, Long> {
    boolean existsByEssayId(Long id);
    ReviewedEssays findByEssayId(Long id);
}
