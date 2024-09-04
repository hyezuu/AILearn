package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 전체 조회
    @GetMapping("/posts")
    public ResponseEntity<Page<PostDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<PostDto> posts = postService.getAllPosts(page, size);
        return new ResponseEntity<>(posts, HttpStatus.valueOf(200));
    }

    // 게시글 상세 조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return new ResponseEntity<>(post, HttpStatus.valueOf(200));
    }

    // 게시글 생성
    @PostMapping("/posts")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, @AuthenticationPrincipal Provider provider) {
        PostDto createdPost = postService.createPost(postDto, provider);
        return new ResponseEntity<>(createdPost, HttpStatus.valueOf(201));
    }

    // 게시글 수정
    @PutMapping("/posts/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody PostDto postDto, @AuthenticationPrincipal Provider provider) {
        PostDto updatedPost = postService.updatePost(id, postDto, provider);
        return new ResponseEntity<>(updatedPost, HttpStatus.valueOf(200));
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @AuthenticationPrincipal Provider provider) {
        postService.deletePost(id, provider);
        return new ResponseEntity<>(HttpStatus.valueOf(204));
    }

    // 내 게시글 목록 조회
    @GetMapping("/me/posts")
    public ResponseEntity<Page<PostDto>> getUserPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @AuthenticationPrincipal Provider provider) {
        Page<PostDto> posts = postService.getPostsByUserId(page, size, provider);
        return new ResponseEntity<>(posts, HttpStatus.valueOf(200));
    }
}