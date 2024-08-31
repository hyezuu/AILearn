package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.comments.Comments;
import com.example.ormi5finalteam1.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByPostId(Post postId);
}
