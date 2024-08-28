package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.comments.Comment;
import com.example.ormi5finalteam1.domain.comments.dto.CommentDto;
import com.example.ormi5finalteam1.repository.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<CommentDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }


    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

}
