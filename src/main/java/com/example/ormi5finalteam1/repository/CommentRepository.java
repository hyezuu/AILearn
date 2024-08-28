package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.comments.dto.CommentDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentDto, Long> {
    List<CommentDto> findByPostId(Long id);
}