package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.comment.Comment;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PostService postService;
    private final CommentService commentService;

    public void changeUserStatus(Long userId) {

        User normalUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if(normalUser.getDeletedAt() == null) {
            if (normalUser.isActive()) normalUser.deactivateUser();
            else normalUser.activateUser();
        }
    }

    public void deleteUser(Long userId) {

        User normalUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (normalUser.getDeletedAt() == null) normalUser.delete();
        else throw new BusinessException(ErrorCode.ALREADY_DELETED);
    }

    public void deletePost(Long postId) {
        Post foundPost = postService.getPostById(postId);
        if (foundPost.getDeletedAt() == null) foundPost.delete();
        else throw new BusinessException(ErrorCode.ALREADY_DELETED);
    }

    public void deleteComment(Long postId, Long commentId) {
        commentService.deleteCommentByAdmin(postId, commentId);
    }
}
