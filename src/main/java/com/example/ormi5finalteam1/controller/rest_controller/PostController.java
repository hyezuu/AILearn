package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.post.dto.PostRequestDto;
import com.example.ormi5finalteam1.domain.post.dto.PostResponseDto;
import com.example.ormi5finalteam1.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(PostRequestDto postRequestDto) {
        PostResponseDto createdPost = postService.createPost(postRequestDto);
        return ResponseEntity.ok(createdPost);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        List<PostResponseDto> Posts = postService.getAllPosts();
        return ResponseEntity.ok(Posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
        PostResponseDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, PostRequestDto postRequestDto) {
        PostResponseDto updatedPost = postService.updatePost(id, postRequestDto);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
