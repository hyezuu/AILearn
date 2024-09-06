package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.like.Like;
import com.example.ormi5finalteam1.domain.like.dto.LikeDto;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public LikeService(LikeRepository likeRepository, UserService userService, PostService postService) {
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.postService = postService;
    }

    // 게시글에 좋아요 누르기
    @Transactional
    public LikeDto likePost(Long postId, Long userId) {
        likeRepository.findByUserIdAndPostId(userId, postId)
                .ifPresent(like -> {
                    throw new BusinessException(ErrorCode.ALREADY_LIKED);
                });

        User user = userService.getUser(userId);
        Post post = postService.getPost(postId);

        Like like = new Like(user, post, LocalDateTime.now());
        likeRepository.save(like);
        return convertToDto(like);
    }

    // 게시글에 좋아요 취소
    @Transactional
    public void unlikePost(Long postId, Provider provider) {
        Long userId = provider.id();
        Like like = likeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LIKE_NOT_FOUND));
        if (!like.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        likeRepository.delete(like);
    }

    // 사용자가 좋아요를 누른 게시글 목록 조회
    public Page<PostDto> getLikedPosts(int page, int size, Provider provider) {
        Long userId = provider.id();
        Pageable pageable = PageRequest.of(page, size);
        return likeRepository.findByUserId(userId, pageable)
                .map(like -> convertToDto(like.getPost()));
            }

    private LikeDto convertToDto(Like like) {
        return new LikeDto(like.getId(), like.getUser().getId(), like.getPost().getId(), like.getCreatedAt());
    }

    private PostDto convertToDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}