package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.comment.dto.CommentDto;
import com.example.ormi5finalteam1.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentsController {
    private final CommentService commentService;

    @Autowired
    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.createComment(commentDto);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping
    public List<CommentDto> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

//    @GetMapping("/user/{userId}")
//    public List<CommentDto> getCommentsByUserId(@PathVariable Long userId) {
//        return commentService.getCommentsByUserId(userId);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @RequestParam Long userId) {
        try {
            commentService.deleteComment(id, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
