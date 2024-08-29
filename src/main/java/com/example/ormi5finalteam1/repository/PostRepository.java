package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
}
