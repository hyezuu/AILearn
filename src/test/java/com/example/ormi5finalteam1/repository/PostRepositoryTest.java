package com.example.ormi5finalteam1.repository;

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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post;

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
    }

    @Test
    void findAllByDeletedAtIsNullOrderByCreatedAtDesc_모든_삭제되지_않은_게시글을_최신순으로_가져올_수_있다() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Post> result = postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(post.getTitle(), result.getContent().get(0).getTitle());
        assertNull(result.getContent().get(0).getDeletedAt());
    }

    @Test
    void findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc_유저의_삭제되지_않은_게시글을_최신순으로_가져올_수_있다() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Post> result = postRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(user.getId(), pageable);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(post.getTitle(), result.getContent().get(0).getTitle());
        assertNull(result.getContent().get(0).getDeletedAt());
    }

    @Test
    void findAllByTitleContainingAndDeletedAtIsNullOrderByCreatedAtDesc_제목에_특정_키워드를_포함하는_게시글을_검색할_수_있다() {
        // Given
        String keyword = "title";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Post> result = postRepository.findAllByTitleContainingAndDeletedAtIsNullOrderByCreatedAtDesc(keyword, pageable);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getTitle().contains(keyword));
        assertNull(result.getContent().get(0).getDeletedAt());
    }
}