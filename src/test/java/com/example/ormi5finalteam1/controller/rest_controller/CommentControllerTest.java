package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.comment.dto.CommentDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CommentDto commentDto;
    private Provider provider;

    @BeforeEach
    void setUp() {
        // Mockito 어노테이션 초기화
        MockitoAnnotations.openMocks(this);

        // MockMvc 초기화
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        // Given: 초기 데이터 설정
        commentDto = new CommentDto(1L, 1L, 1L, "Test Comment");
        provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);
    }

    @Test
    @WithMockUser
    void createComment_댓글을_생성할_수_있다() throws Exception {
        // Given
        when(commentService.createComment(any(CommentDto.class), any(Provider.class))).thenReturn(commentDto);

        // When & Then
        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"postId\":1,\"content\":\"Test Comment\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.content").value(commentDto.getContent()));

        verify(commentService, times(1)).createComment(any(CommentDto.class), any(Provider.class));
    }

    @Test
    @WithMockUser
    void getComments_게시글에_작성된_댓글들을_목록으로_볼_수_있다() throws Exception {
        // Given
        List<CommentDto> comments = new ArrayList<>();
        comments.add(commentDto);
        when(commentService.getCommentsByPostId(anyLong())).thenReturn(comments);

        // When & Then
        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(commentDto.getId()))
                .andExpect(jsonPath("$[0].content").value(commentDto.getContent()));

        verify(commentService, times(1)).getCommentsByPostId(anyLong());
    }

    @Test
    @WithMockUser
    void deleteComment_댓글을_삭제할_수_있다() throws Exception {
        // Given
        doNothing().when(commentService).deleteComment(anyLong(), any(Provider.class));

        // When & Then
        mockMvc.perform(delete("/api/posts/1/comments/1"))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(anyLong(), any(Provider.class));
    }

    @Test
    @WithMockUser
    void deleteComment_권한이_없을_경우_403_에러를_반환한다() throws Exception {
        // Given
        doThrow(new SecurityException("Forbidden")).when(commentService).deleteComment(anyLong(), any(Provider.class));

        // When & Then
        mockMvc.perform(delete("/api/posts/1/comments/1"))
                .andExpect(status().isForbidden());

        verify(commentService, times(1)).deleteComment(anyLong(), any(Provider.class));
    }

    // 추가적인 테스트 케이스 (사용자 별 댓글 조회) 주석 처리된 메소드를 사용할 경우
    // @Test
    // @WithMockUser
    // void getCommentsByUserId_사용자는_자신의_댓글들을_목록으로_볼_수_있다() throws Exception {
    //     List<CommentDto> comments = new ArrayList<>();
    //     comments.add(commentDto);
    //     when(commentService.getCommentsByUserId(anyLong())).thenReturn(comments);
    //
    //     mockMvc.perform(get("/api/posts/1/comments/user/1"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$[0].id").value(commentDto.getId()))
    //             .andExpect(jsonPath("$[0].content").value(commentDto.getContent()));
    //
    //     verify(commentService, times(1)).getCommentsByUserId(anyLong());
    // }
}