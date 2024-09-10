package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.like.Like;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post;
    private Like like;

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

        like = new Like(user, post, LocalDateTime.now());
        like = likeRepository.save(like);
    }

    @Test
    void findByUserIdAndPostId_특정_사용자가_특정_게시글에_좋아요를_눌렀는지_확인할_수_있다() {
        // When
        Optional<Like> result = likeRepository.findByUserIdAndPostId(user.getId(), post.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getUser().getId());
        assertEquals(post.getId(), result.get().getPost().getId());
    }

    @Test
    void findByUserIdAndPostId_존재하지_않는_좋아요를_조회할_때_비어있는_Optional을_반환한다() {
        // When
        Optional<Like> result = likeRepository.findByUserIdAndPostId(999L, post.getId());

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByUserId_사용자가_좋아요를_누른_게시글_목록을_조회할_수_있다() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Like> result = likeRepository.findByUserId(user.getId(), pageable);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(post.getId(), result.getContent().get(0).getPost().getId());
    }

    @Test
    void findByUserId_좋아요를_누른_게시글이_없는_경우_빈_페이지를_반환한다() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Like> result = likeRepository.findByUserId(999L, pageable);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserId_페이징이_적용된_경우_올바른_페이지를_반환한다() {
        // Given
        Post post2 = new Post(user, "title2", "content2");
        post2 = postRepository.save(post2);

        Like like2 = new Like(user, post2, LocalDateTime.now());
        likeRepository.save(like2);

        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<Like> result = likeRepository.findByUserId(user.getId(), pageable);

        // Then
        assertEquals(1, result.getSize());
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
    }
}