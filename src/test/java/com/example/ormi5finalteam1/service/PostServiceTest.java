package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.PostRepository;
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

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllPosts_모든_게시글을_조회할_수_있다() {
        // Given
        Pageable pageable = PageRequest.of(0, 12);
        List<Post> posts = new ArrayList<>();
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        posts.add(new Post(user, "title1", "content1"));
        Page<Post> postPage = new PageImpl<>(posts);

        when(postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable)).thenReturn(postPage);

        // When
        Page<PostDto> result = postService.getAllPosts(0, 12, null);

        // Then
        assertEquals(1, result.getTotalElements());
        verify(postRepository, times(1)).findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
    }

    @Test
    void getAllPosts_게시글을_키워드로_조회할_수_있다() {
        // Given
        Pageable pageable = PageRequest.of(0, 12);
        String keyword = "test";
        List<Post> posts = new ArrayList<>();
        User user = user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        posts.add(new Post(user, "testTitle", "testContent"));
        Page<Post> postPage = new PageImpl<>(posts);

        when(postRepository.findAllByTitleContainingAndDeletedAtIsNullOrderByCreatedAtDesc(keyword, pageable))
                .thenReturn(postPage);

        // When
        Page<PostDto> result = postService.getAllPosts(0, 12, keyword);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("testTitle", result.getContent().get(0).getTitle());
        verify(postRepository, times(1)).findAllByTitleContainingAndDeletedAtIsNullOrderByCreatedAtDesc(keyword, pageable);
    }

    @Test
    void getPostById_게시글이_존재하는_경우_게시글을_반환한다() {
        // Given
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        Post post = new Post(user, "title", "content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When
        PostDto result = postService.getPostById(1L);

        // Then
        assertNotNull(result);
        assertEquals("title", result.getTitle());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void getPostById_게시글이_존재하지_않으면_예외를_던진다() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> postService.getPostById(1L));
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void createPost_게시글을_생성할_수_있다() {
        // Given
        Provider provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);
        User user = user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        PostDto postDto = new PostDto(null, 1L,"nickname", Grade.A1, "title", "content", 0, LocalDateTime.now(), null);
        Post post = new Post(user, "title", "content");

        when(userService.getUser(provider.id())).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // When
        PostDto createdPost = postService.createPost(postDto, provider);

        // Then
        assertNotNull(createdPost);
        assertEquals("title", createdPost.getTitle());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void updatePost_게시글을_수정하고_updatedAt_도_갱신할_수_있다() {
        // Given
        Provider provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        ReflectionTestUtils.setField(user, "id", provider.id());

        Post post = new Post(user, "oldTitle", "oldContent");
        PostDto postDto = new PostDto(1L, 1L, "nickname", Grade.A1, "newTitle", "newContent", 0, LocalDateTime.now(), null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When
        PostDto updatedPost = postService.updatePost(1L, postDto, provider);

        // Then
        assertEquals("newTitle", updatedPost.getTitle());  // title이 "newTitle"로 업데이트되었는지 확인
        assertEquals("newContent", updatedPost.getContent());  // content가 "newContent"로 업데이트되었는지 확인
        assertNotEquals(LocalDateTime.of(2023, 1, 1, 0, 0), updatedPost.getUpdatedAt());  // updatedAt이 갱신되었는지 확인
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void deletePost_게시글을_삭제할_수_있다() {
        // Given
        Provider provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);
        User user = user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        ReflectionTestUtils.setField(user, "id", provider.id());
        Post post = new Post(user, "title", "content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When
        postService.deletePost(1L, provider);

        // Then
        verify(postRepository, times(1)).save(post);
    }
}