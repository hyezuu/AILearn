package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.comment.dto.CommentDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 생성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto, @AuthenticationPrincipal Provider provider) {
        CommentDto createdComment = commentService.createComment(commentDto, provider);
        return new ResponseEntity<>(createdComment, HttpStatus.valueOf(201));
    }

    // 댓글 목록 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return new ResponseEntity<>(comments, HttpStatus.valueOf(200));
    }

    // 댓글 삭제
    @DeleteMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @AuthenticationPrincipal Provider provider) {
        try {
            commentService.deleteComment(id, provider);
            return new ResponseEntity<>(HttpStatus.valueOf(204));
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.valueOf(403));
        }
    }

    // 내 댓글 목록 조회
    @GetMapping("/me/comments")
    public ResponseEntity<Page<CommentDto>> getUserComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @AuthenticationPrincipal Provider provider) {
        Page<CommentDto> comments = commentService.getCommentsByUserId(page, size, provider);
        return new ResponseEntity<>(comments, HttpStatus.valueOf(200));
    }
}
