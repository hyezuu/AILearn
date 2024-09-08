package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.post.dto.AdminPostDetailDto;
import com.example.ormi5finalteam1.domain.post.dto.AdminPostListDto;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.dto.UserInfoDto;
import com.example.ormi5finalteam1.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public List<UserInfoDto> getAllUserList(@AuthenticationPrincipal Provider provider) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        return adminService.getAllUserList();
    }

    @PutMapping("/users/{id}")
    public void changeUserStatus(@AuthenticationPrincipal Provider provider,
                                 @PathVariable("id") Long userId) {
        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        adminService.changeUserStatus(userId);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@AuthenticationPrincipal Provider provider,
                           @PathVariable("id") Long userId) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        adminService.deleteUser(userId);
    }

    @GetMapping("/posts")
    public List<AdminPostListDto> getAllPosts(@AuthenticationPrincipal Provider provider) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        return adminService.getAllPostList();
    }

    @GetMapping("/posts/{id}")
    public AdminPostDetailDto getPostById(@AuthenticationPrincipal Provider provider,
                                          @PathVariable("id") Long postId) {
        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        return adminService.getPostById(postId);
    }

    @DeleteMapping("/posts/{id}")
    public void deletePost(@AuthenticationPrincipal Provider provider,
                           @PathVariable("id") Long postId) {
        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        adminService.deletePost(postId);
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public void deleteComment(@AuthenticationPrincipal Provider provider,
                              @PathVariable("postId") Long postId,
                              @PathVariable("commentId") Long commentId) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        adminService.deleteComment(postId, commentId);
    }
}