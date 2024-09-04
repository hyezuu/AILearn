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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    private User user;
    private Provider provider;
    private Post post;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Given: 테스트에 필요한 객체들을 생성
        user = User.builder()
                .email("test@test.com")
                .password("password")
                .nickname("test")
                .build();
        provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);
        post = new Post(user, "Test Post", "This is a test post content");
        comment = new Comment(user, post, "Test Comment");
        commentDto = new CommentDto(1L, user.getId(), post.getId(), "Test Comment");
    }

    @Test
    void createComment_회원은_게시글에_댓글을_작성할_수_있다() {
        // Given
        when(userService.getUser(provider.id())).thenReturn(user);
        when(postService.getPostById(commentDto.getPostId())).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        CommentDto result = commentService.createComment(commentDto, provider);

        // Then
        assertNotNull(result);
        assertEquals(commentDto.getContent(), result.getContent());
        assertEquals(commentDto.getPostId(), result.getPostId());

        verify(userService, times(1)).getUser(provider.id());
        verify(postService, times(1)).getPostById(commentDto.getPostId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void deleteComment_댓글_작성자는_댓글을_삭제할_수_있다() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        // When
        commentService.deleteComment(1L, provider);

        // Then
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_댓글이_존재하지_않을_경우_예외가_발생한다() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            commentService.deleteComment(1L, provider);
        });

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());

        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void deleteComment_작성자와_삭제하려는_사람이_다를_경우_예외가_발생한다() {
        // Given
        User anotherUser = User.builder()
                .email("another@example.com")
                .password("password")
                .nickname("anotherUser")
                .build();
        Comment anotherComment = new Comment(anotherUser, post, "Another Comment");
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(anotherComment));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            commentService.deleteComment(1L, provider);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void getCommentsByPostId_게시글에_작성된_댓글들을_목록으로_볼_수_있다() {
        // Given
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        when(commentRepository.findByPostId(anyLong())).thenReturn(comments);

        // When
        List<CommentDto> result = commentService.getCommentsByPostId(post.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(comment.getContent(), result.get(0).getContent());

        verify(commentRepository, times(1)).findByPostId(post.getId());
    }

    @Test
    void getCommentsByUserId_사용자는_자신이_작성한_댓글들을_목록으로_볼_수_있다() {
        // Given
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        when(commentRepository.findByUserId(anyLong())).thenReturn(comments);

        // When
        List<CommentDto> result = commentService.getCommentsByUserId(user.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(comment.getContent(), result.get(0).getContent());

        verify(commentRepository, times(1)).findByUserId(user.getId());
    }
}