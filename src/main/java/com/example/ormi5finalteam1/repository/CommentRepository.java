package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUserId(Long id);
    List<Comment> findByPostId(Long id);
}
