package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.comment.Comment;
import com.example.ormi5finalteam1.domain.comment.dto.CommentDto;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.CommentRepository;
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

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createComment_댓글을_작성할_수_있다() {
        // Given
        Provider provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        Post post = new Post(user, "title", "content");
        CommentDto commentDto = new CommentDto(null, 1L, "testUser", 1L, "test post title","comment content", LocalDateTime.now());

        when(userService.getUser(provider.id())).thenReturn(user);
        when(postService.getPost(commentDto.getPostId())).thenReturn(post);

        // When
        CommentDto createdComment = commentService.createComment(commentDto, provider);

        // Then
        assertNotNull(createdComment);
        assertEquals("comment content", createdComment.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void getCommentsByPostId_게시글의_댓글들을_조회할_수_있다() {
        // Given
        Long postId = 1L;
        List<Comment> comments = new ArrayList<>();
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        Post post = new Post(user, "title", "content"); // deletedAt은 기본적으로 null

        // 게시글이 삭제되지 않은 상태를 가정 (별도 설정 불필요)
        comments.add(new Comment(user, post, "comment content"));

        when(postService.getPost(postId)).thenReturn(post);
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(postId)).thenReturn(comments);

        // When
        List<CommentDto> commentDtos = commentService.getCommentsByPostId(postId);

        // Then
        assertEquals(1, commentDtos.size());
        assertEquals("comment content", commentDtos.get(0).getContent());
        verify(postService, times(1)).getPost(postId);
        verify(commentRepository, times(1)).findByPostIdOrderByCreatedAtAsc(postId);
    }

    @Test
    void getCommentsByPostId_삭제된_게시글의_댓글은_조회되지_않는다() {
        // Given
        Long postId = 1L;
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        Post post = new Post(user, "title", "content");

        // 게시글을 삭제된 상태로 변경
        post.delete();

        when(postService.getPost(postId)).thenReturn(post);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            commentService.getCommentsByPostId(postId);
        });

        verify(postService, times(1)).getPost(postId);
        verify(commentRepository, never()).findByPostIdOrderByCreatedAtAsc(postId);
    }

    @Test
    void deleteComment_댓글을_삭제할_수_있다() {
        // Given
        Provider provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        ReflectionTestUtils.setField(user, "id", provider.id());
        Comment comment = new Comment(user, new Post(user, "title", "content"), "comment content");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // When
        commentService.deleteComment(1L, provider);

        // Then
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_작성자가_아닌_경우_예외를_던진다() {
        // Given
        Provider provider = new Provider(2, "another@example.com", "test", Role.USER, Grade.A1, 0);
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Comment comment = new Comment(user, new Post(user, "title", "content"), "comment content");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> commentService.deleteComment(1L, provider));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(commentRepository, times(0)).delete(comment);
    }

    @Test
    void getCommentsByUserId_사용자가_작성한_댓글을_조회할_수_있다() {
        // Given
        Provider provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();

        Post post = new Post(user, "title", "content");
        Comment comment = new Comment(user, post, "comment content");
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        Pageable pageable = PageRequest.of(1, 10);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(userService.getUser(provider.id())).thenReturn(user);
        when(commentRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)).thenReturn(commentPage);

        // When
        Page<CommentDto> resultPage = commentService.getCommentsByUserId(1, 10, provider);

        // Then
        List<CommentDto> commentDtos = resultPage.getContent();
        assertEquals(1, commentDtos.size());
        assertEquals("comment content", commentDtos.get(0).getContent());
        verify(commentRepository, times(1)).findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
    }
}