package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.comment.dto.CommentDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.service.CommentService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = CommentController.class)
@WithMockUser(username = "test")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Provider provider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(provider, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createComment_사용자는_댓글을_작성할_수_있다() throws Exception {
        // Given
        CommentDto commentDto = new CommentDto(null, 1L, "testUser", 1L, "test post title", "comment content", LocalDateTime.now());
        CommentDto createdComment = new CommentDto(1L, 1L, "testUser", 1L, "test post title", "comment content", LocalDateTime.now());

        when(commentService.createComment(any(CommentDto.class), any(Provider.class))).thenReturn(createdComment);

        // When & Then
        mockMvc.perform(post("/api/posts/{postId}/comments", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("comment content"));

        verify(commentService, times(1)).createComment(any(CommentDto.class), any(Provider.class));
    }

    @Test
    void getComments_게시글의_댓글_목록을_조회할_수_있다() throws Exception {
        // Given
        List<CommentDto> comments = Collections.singletonList(
                new CommentDto(1L, 1L, "testUser", 1L, "test post title", "comment content", LocalDateTime.now())
        );
        when(commentService.getCommentsByPostId(1L)).thenReturn(comments);

        // When & Then
        mockMvc.perform(get("/api/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("comment content"));

        verify(commentService, times(1)).getCommentsByPostId(1L);
    }

    @Test
    void deleteComment_사용자는_댓글을_삭제할_수_있다() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", 1L, 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(eq(1L), any(Provider.class));
    }

    @Test
    void getUserComments_사용자는_자신이_작성한_댓글_목록을_조회할_수_있다() throws Exception {
        // Given
        Page<CommentDto> comments = new PageImpl<>(Collections.singletonList(
                new CommentDto(1L, 1L, "testUser", 1L, "test post title", "comment content", LocalDateTime.now())
        ));
        when(commentService.getCommentsByUserId(0, 12, provider)).thenReturn(comments);

        // When & Then
        mockMvc.perform(get("/api/me/comments")
                        .param("page", "0")
                        .param("size", "12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("comment content"));

        verify(commentService, times(1)).getCommentsByUserId(0, 12, provider);
    }

    @Test
    void deleteComment_권한이_없는_사용자는_댓글을_삭제할_수_없다() throws Exception {
        // Given
        doThrow(new SecurityException("No permission")).when(commentService).deleteComment(anyLong(), any(Provider.class));

        // When & Then
        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", 1L, 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(commentService, times(1)).deleteComment(anyLong(), any(Provider.class));
    }

}