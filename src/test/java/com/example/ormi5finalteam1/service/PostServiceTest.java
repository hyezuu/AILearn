package com.example.ormi5finalteam1.service;

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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
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

    private Provider provider;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);
        user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        ReflectionTestUtils.setField(user, "id", provider.id());
        post = new Post(user, "title", "content");
    }

    @Test
    void getAllPosts_는_모든_게시글을_페이지네이션으로_조회할_수_있다() {
        // Given: 게시글이 존재하고, 페이지 요청이 주어졌을 때
        Page<Post> posts = new PageImpl<>(Collections.singletonList(post));
        when(postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(any(PageRequest.class))).thenReturn(posts);

        // When: getAllPosts 메서드를 호출하여 모든 게시글을 조회하면
        Page<PostDto> result = postService.getAllPosts(0, 1);

        // Then: 게시글이 정상적으로 반환된다.
        assertEquals(1, result.getTotalElements());
        verify(postRepository, times(1)).findAllByDeletedAtIsNullOrderByCreatedAtDesc(any(PageRequest.class));
    }

    @Test
    void getPostById_는_게시글_ID로_게시글을_조회할_수_있다() {
        // Given: 특정 ID를 가진 게시글이 존재할 때
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When: getPostById 메서드를 호출하여 해당 ID로 게시글을 조회하면
        Post result = postService.getPostById(1L);

        // Then: 해당 게시글이 반환된다.
        assertEquals(post, result);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void createPost_는_새로운_게시글을_생성할_수_있다() {
        // Given: 사용자가 존재하고, 게시글 데이터가 주어졌을 때
        when(userService.getUser(provider.id())).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // When: createPost 메서드를 호출하여 새로운 게시글을 생성하면
        PostDto postDto = postService.createPost(new PostDto(), provider);

        // Then: 게시글이 저장되고, 저장된 게시글이 반환된다.
        assertNotNull(postDto);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void updatePost_는_기존_게시글을_수정할_수_있다() {
        // Given: 특정 ID를 가진 게시글이 존재하고, 수정할 데이터가 주어졌을 때
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // When: updatePost 메서드를 호출하여 해당 게시글을 수정하면
        PostDto postDto = postService.updatePost(1L, new PostDto(), provider);

        // Then: 게시글이 수정되고, 수정된 게시글이 반환된다.
        assertNotNull(postDto);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void deletePost_는_게시글을_삭제할_수_있다() {
        // Given: 특정 ID를 가진 게시글이 존재할 때
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When: deletePost 메서드를 호출하여 해당 게시글을 삭제하면
        postService.deletePost(1L, provider);

        // Then: 게시글이 소프트 삭제되고, 삭제된 상태가 저장된다.
        verify(postRepository, times(1)).save(post);
        assertNotNull(post.getDeletedAt());
    }
}