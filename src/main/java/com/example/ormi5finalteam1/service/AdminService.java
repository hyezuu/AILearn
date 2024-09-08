package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.comment.dto.AdminCommentDto;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.AdminPostDetailDto;
import com.example.ormi5finalteam1.domain.post.dto.AdminPostListDto;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.user.dto.UserInfoDto;
import com.example.ormi5finalteam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PostService postService;
    private final CommentService commentService;

    public List<UserInfoDto> getAllUserList() {

        return userRepository.findAllByOrderByRoleAscId().stream()
                .map(UserInfoDto::toDto).collect(Collectors.toList());
    }
    public void changeUserStatus(Long userId) {

        User normalUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (normalUser.getRole() == Role.ADMIN) throw new BusinessException(ErrorCode.HAS_ADMIN_AUTHORITY);
        if(normalUser.getDeletedAt() == null) {
            if (normalUser.isActive()) normalUser.deactivateUser();
            else normalUser.activateUser();
        }
    }

    public void deleteUser(Long userId) {

        User normalUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (normalUser.getRole() == Role.ADMIN) throw new BusinessException(ErrorCode.HAS_ADMIN_AUTHORITY);
        if (normalUser.getDeletedAt() == null) normalUser.delete();
        else throw new BusinessException(ErrorCode.ALREADY_DELETED);
    }

    public void deletePost(Long postId) {
        Post foundPost = postService.getPost(postId);
        if (foundPost.getDeletedAt() == null) foundPost.delete();
        else throw new BusinessException(ErrorCode.ALREADY_DELETED);
    }

    public void deleteComment(Long postId, Long commentId) {
        commentService.deleteCommentByAdmin(postId, commentId);
    }

    public List<AdminPostListDto> getAllPostList() {

        return postService.getAllPostsByAdmin().stream().map(AdminPostListDto::toDto).collect(Collectors.toList());
    }

    public AdminPostDetailDto getPostById(Long postId) {

        return AdminPostDetailDto.toDto(postService.getPostByAdmin(postId),
                commentService.getCommentsByPostId(postId).stream().map(AdminCommentDto::toDto).toList());
    }
}