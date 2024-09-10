package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.comment.Comment;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testUser")
                .build();
        user = userRepository.save(user);

        post = new Post(user, "title", "content");
        post = postRepository.save(post);

        comment = new Comment(user, post, "comment content");
        comment = commentRepository.save(comment);
    }

    @Test
    void findByPostIdOrderByCreatedAtAsc_게시글에_대한_댓글을_작성순으로_조회할_수_있다() {
        // When
        List<Comment> result = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId());

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(comment.getContent(), result.get(0).getContent());
        assertEquals(post.getId(), result.get(0).getPost().getId());
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_사용자가_작성한_댓글을_최신순으로_조회할_수_있다() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Comment> result = commentRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(comment.getContent(), result.getContent().get(0).getContent());
        assertEquals(user.getId(), result.getContent().get(0).getUser().getId());
    }

    @Test
    void findByIdAndPostId_특정_게시글의_특정_댓글을_조회할_수_있다() {
        // When
        Comment result = commentRepository.findByIdAndPostId(comment.getId(), post.getId());

        // Then
        assertNotNull(result);
        assertEquals(comment.getContent(), result.getContent());
        assertEquals(post.getId(), result.getPost().getId());
    }

    @Test
    void findByIdAndPostId_존재하지_않는_댓글을_조회할_때_null을_반환한다() {
        // When
        Comment result = commentRepository.findByIdAndPostId(999L, post.getId());

        // Then
        assertNull(result);
    }

    @Test
    void findByPostIdOrderByCreatedAtAsc_댓글이_없는_게시글일_경우_빈_리스트를_반환한다() {
        // Given
        Post newPost = new Post(user, "new title", "new content");
        newPost = postRepository.save(newPost);

        // When
        List<Comment> result = commentRepository.findByPostIdOrderByCreatedAtAsc(newPost.getId());

        // Then
        assertTrue(result.isEmpty());
    }
}