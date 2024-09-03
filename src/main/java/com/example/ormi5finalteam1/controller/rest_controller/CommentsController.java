package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.comment.dto.CommentDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto, @AuthenticationPrincipal Provider provider) {
        CommentDto createdComment = commentService.createComment(commentDto, provider);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping
    public List<CommentDto> getComments(@PathVariable Long postId, @AuthenticationPrincipal Provider provider) {
        return commentService.getCommentsByPostId(postId, provider);
    }

//    @GetMapping("/user/{userId}")
//    public List<CommentDto> getCommentsByUserId(@PathVariable Long userId, @AuthenticationPrincipal Provider provider) {
//        return commentService.getCommentsByUserId(userId, provider);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @AuthenticationPrincipal Provider provider) {
        try {
            commentService.deleteComment(id, provider);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
