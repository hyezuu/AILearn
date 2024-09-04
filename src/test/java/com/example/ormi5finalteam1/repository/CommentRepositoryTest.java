package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.comment.Comment;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentRepositoryTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    private User user;
    private Post post;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByUserId_는_유저의_댓글을_가져올_수_있다() {
        // Given: Mock 데이터와 리포지토리 동작 설정
        user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("test")
                .build();
        post = new Post(user, "Test Post", "This is a test post content");
        comment1 = new Comment(user, post, "Test Comment 1");
        comment2 = new Comment(user, post, "Test Comment 2");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commentRepository.findByUserId(user.getId())).thenReturn(List.of(comment1, comment2));

        // When: `findByUserId` 메소드 호출
        List<Comment> comments = commentRepository.findByUserId(user.getId());

        // Then: 결과 검증
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("Test Comment 1", comments.get(0).getContent());
        assertEquals("Test Comment 2", comments.get(1).getContent());

        // Then: 메소드 호출 검증
        verify(commentRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void findByPostId_는_게시글의_댓글을_가져올_수_있다() {
        // Given: Mock 데이터와 리포지토리 동작 설정
        user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("test")
                .build();
        post = new Post(user, "Test Post", "This is a test post content");
        comment1 = new Comment(user, post, "Test Comment 1");
        comment2 = new Comment(user, post, "Test Comment 2");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commentRepository.findByPostId(post.getId())).thenReturn(List.of(comment1, comment2));

        // When: `findByPostId` 메소드 호출
        List<Comment> comments = commentRepository.findByPostId(post.getId());

        // Then: 결과 검증
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals("Test Comment 1", comments.get(0).getContent());
        assertEquals("Test Comment 2", comments.get(1).getContent());

        // Then: 메소드 호출 검증
        verify(commentRepository, times(1)).findByPostId(post.getId());
    }
}