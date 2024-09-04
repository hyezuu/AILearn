package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostRepositoryTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        post = new Post(user, "title", "content");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post postArg = invocation.getArgument(0);
            return Post.builder()  // 빌더 패턴을 사용하여 새롭게 Post 객체를 생성하여 반환
                    .id(1L)  // 임의의 ID 설정
                    .user(postArg.getUser())
                    .title(postArg.getTitle())
                    .content(postArg.getContent())
                    .build();
        });
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(post)));
    }

    @Test
    void findAllByDeletedAtIsNullOrderByCreatedAtDesc_는_삭제되지_않은_게시글을_최신순으로_가져올_수_있다() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(post.getTitle(), result.getContent().get(0).getTitle());
        assertNull(result.getContent().get(0).getDeletedAt());
    }

    @Test
    void findById_는_존재하는_게시글을_찾을_수_있다() {
        Optional<Post> result = postRepository.findById(post.getId());

        assertTrue(result.isPresent());
        assertEquals(post.getId(), result.get().getId());
    }

    @Test
    void findById_는_존재하지_않는_게시글을_찾지_못한다() {
        when(postRepository.findById(-1L)).thenReturn(Optional.empty());

        Optional<Post> result = postRepository.findById(-1L);

        assertFalse(result.isPresent());
    }

    @Test
    void save_는_새로운_게시글을_저장할_수_있다() {
        Post newPost = new Post(user, "new title", "new content");

        Post savedPost = postRepository.save(newPost);

        assertNotNull(savedPost.getId());  // 이제 ID는 null이 아님
        assertEquals("new title", savedPost.getTitle());
        assertEquals("new content", savedPost.getContent());
    }

    @Test
    void delete_는_게시글을_삭제할_수_있다() {
        doNothing().when(postRepository).delete(post);

        postRepository.delete(post);

        verify(postRepository, times(1)).delete(post);  // delete 메서드가 한 번 호출되었는지 검증
    }
}