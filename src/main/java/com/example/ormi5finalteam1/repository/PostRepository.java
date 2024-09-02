package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PagingAndSortingRepository<Post, Long> {
    Page<Post> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
}
