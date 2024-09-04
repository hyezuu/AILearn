package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private Provider provider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        provider = new Provider(1, "test@example.com", "test", Role.USER, Grade.A1, 0);
    }

    @Test
    void getAllPosts_는_데이터없음_예외발생시_BusinessException을_던진다() throws Exception {
        // given
        when(postService.getAllPosts(anyInt(), anyInt())).thenThrow(new BusinessException(ErrorCode.POST_NOT_FOUND));

        // when
        ResponseEntity<Page<PostDto>> response = postController.getAllPosts(0, 12);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(postService, times(1)).getAllPosts(anyInt(), anyInt());
    }

    @Test
    void getPostById_는_존재하지않는_게시물ID로_조회시_BusinessException을_던진다() throws Exception {
        // given
        when(postService.getPostById(anyLong())).thenThrow(new BusinessException(ErrorCode.POST_NOT_FOUND));

        // when
        ResponseEntity<Post> response = postController.getPostById(1L);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(postService, times(1)).getPostById(anyLong());
    }

    @Test
    void updatePost_는_수정권한없음_예외발생시_BusinessException을_던진다() throws Exception {
        // given
        PostDto mockPostDto = new PostDto();
        when(postService.updatePost(anyLong(), any(PostDto.class), any(Provider.class)))
                .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        // when
        ResponseEntity<PostDto> response = postController.updatePost(1L, mockPostDto, provider);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(postService, times(1)).updatePost(anyLong(), any(PostDto.class), any(Provider.class));
    }

    @Test
    void deletePost_는_삭제권한없음_예외발생시_BusinessException을_던진다() throws Exception {
        // given
        doThrow(new BusinessException(ErrorCode.USER_NOT_FOUND)).when(postService).deletePost(anyLong(), any(Provider.class));

        // when
        ResponseEntity<Void> response = postController.deletePost(1L, provider);

        // then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(postService, times(1)).deletePost(anyLong(), any(Provider.class));
    }

    @Test
    void deletePost_는_존재하지않는_게시물ID로_삭제시_BusinessException을_던진다() throws Exception {
        // given
        doThrow(new BusinessException(ErrorCode.POST_NOT_FOUND)).when(postService).deletePost(anyLong(), any(Provider.class));

        // when
        ResponseEntity<Void> response = postController.deletePost(1L, provider);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(postService, times(1)).deletePost(anyLong(), any(Provider.class));
    }
}