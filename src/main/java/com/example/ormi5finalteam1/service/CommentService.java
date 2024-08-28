package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.comments.Comments;
import com.example.ormi5finalteam1.domain.comments.dto.CommentsDto;
import com.example.ormi5finalteam1.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<CommentsDto> getComments() {
        return commentRepository.findAll().stream()
                .map(CommentsDto::fromEntity)
                .collect(Collectors.toList());
    }

    public CommentsDto createComment(CommentsDto commentDto) {
        Comments comment = CommentsDto.toEntity(commentDto);
        comment.setCreatedAt(LocalDateTime.now());
        Comments savedComment = commentRepository.save(comment);
        return CommentsDto.fromEntity(savedComment);
    }

    public boolean deleteComment(Long id) {
        return commentRepository.findById(id)
                .map(comment -> {
                    commentRepository.delete(comment);
                    return true;
                })
                .orElse(false);
    }

}