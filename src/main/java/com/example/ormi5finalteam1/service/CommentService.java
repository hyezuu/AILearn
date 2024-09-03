package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.comment.Comment;
import com.example.ormi5finalteam1.domain.comment.dto.CommentDto;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;


    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService, PostService postService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postService = postService;
    }

    // 회원은 게시글에 댓글을 작성할 수 있다.
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Provider provider) {
        User user = userService.getUser(provider.id());
        Post post = postService.getPostById(commentDto.getPostId());
        Comment comment = new Comment(user, post, commentDto.getContent());
        commentRepository.save(comment);
        return convertToDto(comment);
    }

    // 게시글에 작성된 댓글들을 목록으로 볼 수 있다.
    public List<CommentDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 댓글 작성자는 댓글을 삭제할 수 있다. (hard delete)
    @Transactional
    public void deleteComment(Long id, Provider provider) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getUser().getId().equals(provider.id())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        commentRepository.delete(comment);
    }

    // 사용자는 자신이 작성한 댓글들을 목록으로 볼 수 있다.
    public List<CommentDto> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CommentDto convertToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getPost().getId(),
                comment.getContent()
        );
    }
}