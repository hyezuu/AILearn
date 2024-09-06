package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long id);
    Page<Comment> findByUserIdOrderByCreatedAtDesc(Long id, Pageable pageable);

    Comment findByIdAndPostId(@Param("id") Long commentId,
                              @Param("postId") Long postId);
}