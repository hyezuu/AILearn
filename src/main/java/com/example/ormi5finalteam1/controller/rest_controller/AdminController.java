package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.service.AdminService;
import com.example.ormi5finalteam1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

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
