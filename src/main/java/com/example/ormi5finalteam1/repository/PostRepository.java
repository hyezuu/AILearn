package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.post.Post;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PagingAndSortingRepository<Post, Long> {
    Page<Post> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Post> findAllByTitleContainingAndDeletedAtIsNullOrderByCreatedAtDesc(String keyword, Pageable pageable);
}
