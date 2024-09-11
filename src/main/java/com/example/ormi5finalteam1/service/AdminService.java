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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PostService postService;
    private final CommentService commentService;

    public Page<UserInfoDto> getAllUserList(Pageable pageable, String nickname) {

        // 키워드가 null 이거나 빈 문자열이면 전체 조회, 그렇지 않으면 검색
        if (nickname == null || nickname.trim().isEmpty()) {
            return userRepository.findAllByOrderByRoleAscId(pageable).map(UserInfoDto::toDto);
        } else {
            return userRepository.findByNicknameContaining(nickname, pageable).map(UserInfoDto::toDto);
        }
    }
    public void changeUserStatus(Long userId) {

        User normalUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (normalUser.getRole() == Role.ADMIN) throw new BusinessException(ErrorCode.HAS_ADMIN_AUTHORITY);
        if (normalUser.getDeletedAt() == null) {
            if (normalUser.isActive()) normalUser.deactivateUser();
            else normalUser.activateUser();
        }
    }

    public LocalDateTime deleteUser(Long userId) {

        User normalUser = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (normalUser.getRole() == Role.ADMIN) throw new BusinessException(ErrorCode.HAS_ADMIN_AUTHORITY);
        if (normalUser.getDeletedAt() == null) normalUser.delete();
        else throw new BusinessException(ErrorCode.ALREADY_DELETED);

        return normalUser.getDeletedAt();
    }

    public void deletePost(Long postId) {
        Post foundPost = postService.getPost(postId);
        if (foundPost.getDeletedAt() == null) foundPost.delete();
        else throw new BusinessException(ErrorCode.ALREADY_DELETED);
    }

    public void deleteComment(Long postId, Long commentId) {
        commentService.deleteCommentByAdmin(postId, commentId);
    }

    public Page<AdminPostListDto> getAllPostList(Pageable pageable, String keyword) {

        return postService.getAllPostsByAdmin(pageable, keyword);
    }

    public AdminPostDetailDto getPostById(Long postId) {

        return AdminPostDetailDto.toDto(postService.getPostByAdmin(postId),
                commentService.getCommentsByPostIdWithAdmin(postId).stream().map(AdminCommentDto::toDto).toList());
    }
}
