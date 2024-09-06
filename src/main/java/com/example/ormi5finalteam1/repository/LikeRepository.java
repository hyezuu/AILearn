package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.like.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId); // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    Page<Like> findByUserId(Long userId, Pageable pageable); // 사용자가 좋아요를 누른 게시글 목록 조회 (페이지네이션 적용)
}