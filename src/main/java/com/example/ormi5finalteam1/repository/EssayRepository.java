package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.essay.Essay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EssayRepository extends JpaRepository<Essay, Long> {
    Page<Essay> findByUserId(Long id, Pageable pageable);
}
