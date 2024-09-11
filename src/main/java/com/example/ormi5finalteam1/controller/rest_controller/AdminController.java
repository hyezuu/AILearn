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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public Page<UserInfoDto> getAllUserList(@AuthenticationPrincipal Provider provider,
                                            Pageable pageable, String nickname) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        return adminService.getAllUserList(pageable, nickname);
    }

    @PutMapping("/users/{id}")
    public void changeUserStatus(@AuthenticationPrincipal Provider provider,
                                 @PathVariable("id") Long userId) {
        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        adminService.changeUserStatus(userId);
    }

    @DeleteMapping("/users/{id}")
    public LocalDateTime deleteUser(@AuthenticationPrincipal Provider provider,
                                    @PathVariable("id") Long userId) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        return adminService.deleteUser(userId);
    }

    @GetMapping("/posts")
    public Page<AdminPostListDto> getAllPosts(@AuthenticationPrincipal Provider provider,
                                              Pageable pageable,
                                              @RequestParam(required = false) String keyword) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        return adminService.getAllPostList(pageable, keyword);
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
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@AuthenticationPrincipal Provider provider,
                              @PathVariable("postId") Long postId,
                              @PathVariable("commentId") Long commentId) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        adminService.deleteComment(postId, commentId);
    }
}
