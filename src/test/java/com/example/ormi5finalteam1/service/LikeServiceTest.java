package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.like.Like;
import com.example.ormi5finalteam1.domain.like.dto.LikeDto;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void likePost_게시글에_좋아요를_누를_수_있다() {
        // Given
        Provider provider = new Provider(1, "test@example.com", "test", Role.USER, null, 0);
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("nickname")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Post post = new Post(user, "title", "content");
        ReflectionTestUtils.setField(post, "id", 1L);
        Long userId = user.getId();
        Long postId = post.getId();

        when(likeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Optional.empty());
        when(userService.getUser(userId)).thenReturn(user);
        when(postService.getPost(postId)).thenReturn(post);

        // When
        LikeDto likeDto = likeService.likePost(postId, userId);

        // Then
        assertNotNull(likeDto);
        assertEquals(userId, likeDto.getUserId());
        assertEquals(postId, likeDto.getPostId());
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void likePost_이미_좋아요를_누른_경우_예외를_던진다() {
        // Given
        Long postId = 1L;
        Long userId = 1L;
        Provider provider = new Provider(userId, "test@example.com", "test", Role.USER, null, 0);
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("nickname")
                .build();
        Post post = new Post(user, "title", "content");
        Like like = new Like(user, post, LocalDateTime.now());

        when(likeRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Optional.of(like));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> likeService.likePost(postId, userId));
        assertEquals(ErrorCode.ALREADY_LIKED, exception.getErrorCode());
    }

    @Test
    void unlikePost_좋아요를_취소할_수_있다() {
        // Given
        Provider provider = new Provider(1, "test@example.com", "test", Role.USER, null, 0);
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("nickname")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Post post = new Post(user, "title", "content");
        ReflectionTestUtils.setField(post, "id", 1L);
        Like like = new Like(user, post, LocalDateTime.now());

        when(likeRepository.findByUserIdAndPostId(provider.id(), post.getId())).thenReturn(Optional.of(like));

        // When
        likeService.unlikePost(post.getId(), provider);

        // Then
        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void unlikePost_좋아요가_없는_경우_예외를_던진다() {
        // Given
        Long postId = 1L;
        Provider provider = new Provider(1, "test@example.com", "test", Role.USER, null, 0);

        when(likeRepository.findByUserIdAndPostId(provider.id(), postId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> likeService.unlikePost(postId, provider));
        assertEquals(ErrorCode.LIKE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getLikedPosts_사용자가_좋아요를_누른_게시글을_조회할_수_있다() {
        // Given
        Provider provider = new Provider(1, "test@example.com", "test", Role.USER, null, 0);
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("nickname")
                .build();
        Post post = new Post(user, "title", "content");
        Like like = new Like(user, post, LocalDateTime.now());

        List<Like> likes = new ArrayList<>();
        likes.add(like);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Like> likePage = new PageImpl<>(likes, pageable, likes.size());

        when(likeRepository.findByUserId(provider.id(), pageable)).thenReturn(likePage);

        // When
        Page<PostDto> resultPage = likeService.getLikedPosts(0, 10, provider);

        // Then
        assertEquals(1, resultPage.getContent().size());
        assertEquals("title", resultPage.getContent().get(0).getTitle());
        verify(likeRepository, times(1)).findByUserId(provider.id(), pageable);
    }
}