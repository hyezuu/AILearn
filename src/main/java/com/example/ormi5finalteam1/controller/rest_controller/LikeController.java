package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.like.dto.LikeDto;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // 좋아요 누르기
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<LikeDto> likePost(@PathVariable Long postId, @AuthenticationPrincipal Provider provider) {
        LikeDto like = likeService.likePost(postId, provider.id());
        return new ResponseEntity<>(like, HttpStatus.valueOf(201));
    }

    // 좋아요 취소
    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId, @AuthenticationPrincipal Provider provider) {
        try {
            likeService.unlikePost(postId, provider);
            return new ResponseEntity<>(HttpStatus.valueOf(204));
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.valueOf(403));
        }
    }

    // 사용자가 좋아요를 누른 게시글 목록 조회
    @GetMapping("/me/likes")
    public ResponseEntity<Page<PostDto>> getLikedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @AuthenticationPrincipal Provider provider) {
        Page<PostDto> likedPosts = likeService.getLikedPosts(page, size, provider);
        return new ResponseEntity<>(likedPosts, HttpStatus.valueOf(200));
    }
}