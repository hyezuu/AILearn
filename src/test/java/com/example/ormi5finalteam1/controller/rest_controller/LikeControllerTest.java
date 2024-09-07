package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.like.dto.LikeDto;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.service.LikeService;
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
@WebMvcTest(controllers = LikeController.class)
@WithMockUser(username = "test")
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Provider provider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        provider = new Provider(1L, "test@example.com", "test", Role.USER, Grade.A1, 0);

        // 인증된 사용자 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(provider, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void likePost_사용자는_게시글에_좋아요를_누를_수_있다() throws Exception {
        // Given
        LikeDto likeDto = new LikeDto(1L, provider.id(), 1L, LocalDateTime.now());

        when(likeService.likePost(anyLong(), anyLong())).thenReturn(likeDto);

        // When & Then
        mockMvc.perform(post("/api/posts/{postId}/like", 1L)
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())  // 201 응답 상태 코드 확인
                .andExpect(jsonPath("$.userId").value(provider.id()))  // 응답 JSON에서 "userId" 필드 확인
                .andExpect(jsonPath("$.postId").value(1L));  // 응답 JSON에서 "postId" 필드 확인

        verify(likeService, times(1)).likePost(anyLong(), anyLong());
    }

    @Test
    void unlikePost_사용자는_게시글에_좋아요를_취소할_수_있다() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/posts/{postId}/like", 1L)
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());  // 204 응답 상태 코드 확인

        verify(likeService, times(1)).unlikePost(anyLong(), any(Provider.class));
    }

    @Test
    void unlikePost_권한_없는_사용자는_좋아요를_취소할_수_없다() throws Exception {
        // Given
        doThrow(new SecurityException("No permission")).when(likeService).unlikePost(anyLong(), any(Provider.class));

        // When & Then
        mockMvc.perform(delete("/api/posts/{postId}/like", 1L)
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());  // 403 응답 상태 코드 확인

        verify(likeService, times(1)).unlikePost(anyLong(), any(Provider.class));
    }

    @Test
    void getLikedPosts_사용자는_좋아요한_게시글을_조회할_수_있다() throws Exception {
        // Given
        Page<PostDto> likedPosts = new PageImpl<>(Collections.singletonList(
                new PostDto(1L, provider.id(), "nickname", "title", "content", 0, LocalDateTime.now(), LocalDateTime.now())
        ));
        when(likeService.getLikedPosts(anyInt(), anyInt(), any(Provider.class))).thenReturn(likedPosts);

        // When & Then
        mockMvc.perform(get("/api/me/likes")
                        .param("page", "0")
                        .param("size", "12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // 200 응답 상태 코드 확인
                .andExpect(jsonPath("$.content[0].title").value("title"));  // 응답 JSON에서 "title" 필드 확인

        verify(likeService, times(1)).getLikedPosts(anyInt(), anyInt(), any(Provider.class));
    }
}