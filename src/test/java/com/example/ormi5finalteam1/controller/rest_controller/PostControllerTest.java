package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = PostController.class)
@WithMockUser(username = "test")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    private Provider provider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);

        // 인증된 사용자 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(provider, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getAllPosts_사용자는_게시글_목록을_조회할_수_있다() throws Exception {
        // Given
        Page<PostDto> posts = new PageImpl<>(Collections.singletonList(
                new PostDto(1L, 1L, "nickname", Grade.A1, "title", "content", 0, LocalDateTime.now(), null)));
        when(postService.getAllPosts(0, 12, null)).thenReturn(posts);

        // When & Then
        mockMvc.perform(get("/api/posts")
                        .param("page", "0")
                        .param("size", "12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("title"));

        verify(postService, times(1)).getAllPosts(0, 12, null);
    }

    @Test
    void getAllPosts_WithKeyword_게시글을_키워드로_조회할_수_있다() throws Exception {
        // Given
        String keyword = "test";
        Page<PostDto> posts = new PageImpl<>(Collections.singletonList(
                new PostDto(1L, 1L, "nickname", Grade.A1, "testTitle", "content", 0, LocalDateTime.now(), null)));
        when(postService.getAllPosts(0, 12, keyword)).thenReturn(posts);

        // When & Then
        mockMvc.perform(get("/api/posts")
                        .param("page", "0")
                        .param("size", "12")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("testTitle"));

        verify(postService, times(1)).getAllPosts(0, 12, keyword);
    }

    @Test
    void getPostById_사용자는_단일_게시글_상세_내용을_조회할_수_있다() throws Exception {
        // Given
        PostDto postDto = new PostDto(1L, 1L, "nickname", Grade.A1, "title", "content", 0, LocalDateTime.now(), null);
        when(postService.getPostById(1L)).thenReturn(postDto);

        // When & Then
        mockMvc.perform(get("/api/posts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"));

        verify(postService, times(1)).getPostById(1L);
    }

    @Test
    void createPost_사용자는_게시글을_작성할_수_있다() throws Exception {
        // Given
        PostDto postDto = new PostDto(null, 1L, "nickname", Grade.A1, "title", "content", 0, LocalDateTime.now(), null);
        PostDto createdPost = new PostDto(1L, 1L, "nickname", Grade.A1, "title", "content", 0, LocalDateTime.now(), null);

        when(postService.createPost(any(PostDto.class), any(Provider.class))).thenReturn(createdPost);

        // When & Then
        mockMvc.perform(post("/api/posts")
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())  // 201 응답 상태 코드 확인
                .andExpect(jsonPath("$.title").value("title"));  // 응답 JSON에서 "title" 필드 확인

        verify(postService, times(1)).createPost(any(PostDto.class), any(Provider.class));  // 서비스 메서드 호출 확인
    }

    @Test
    void updatePost_작성자는_게시글을_수정할_수_있다() throws Exception {
        // Given
        PostDto postDto = new PostDto(1L, 1L, "nickname", Grade.A1, "newTitle", "newContent", 0, LocalDateTime.now(), null);
        when(postService.updatePost(eq(1L), any(PostDto.class), any(Provider.class))).thenReturn(postDto);

        // When & Then
        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("newTitle"));

        verify(postService, times(1)).updatePost(eq(1L), any(PostDto.class), any(Provider.class));
    }

    @Test
    void deletePost_작성자는_게시글을_삭제할_수_있다() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/posts/{id}", 1L)
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(postService, times(1)).deletePost(eq(1L), any(Provider.class));
    }

    @Test
    void getUserPosts_사용자는_자신이_작성한_게시글_목록을_조회할_수_있다() throws Exception {
        // Given
        Page<PostDto> posts = new PageImpl<>(Collections.singletonList(
                new PostDto(1L, 1L, "nickname", Grade.A1, "title", "content", 0, LocalDateTime.now(), null)));
        when(postService.getPostsByUserId(0, 12, provider)).thenReturn(posts);

        // When & Then
        mockMvc.perform(get("/api/me/posts")
                        .param("page", "0")
                        .param("size", "12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("title"));

        verify(postService, times(1)).getPostsByUserId(0, 12, provider);
    }
}