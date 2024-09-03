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
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto, @AuthenticationPrincipal Provider provider) {
        CommentDto createdComment = commentService.createComment(commentDto, provider);
        return new ResponseEntity<>(createdComment, HttpStatus.valueOf(201));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return new ResponseEntity<>(comments, HttpStatus.valueOf(200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @AuthenticationPrincipal Provider provider) {
        try {
            commentService.deleteComment(id, provider);
            return new ResponseEntity<>(HttpStatus.valueOf(204));
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.valueOf(403));
        }
    }

    //    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<CommentDto>> getCommentsByUserId(@PathVariable Long userId, @AuthenticationPrincipal Provider provider) {
//        List<CommentDto> comments = commentService.getCommentsByUserId(userId, provider);
//        return new ResponseEntity<>(comments, HttpStatusCode.valueOf(200)); // HTTP 200 OK
//    }
}
