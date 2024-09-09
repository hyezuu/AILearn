package com.example.ormi5finalteam1;

import com.example.ormi5finalteam1.domain.comment.dto.CommentDto;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.domain.user.dto.UpdateUserRequestDto;
import com.example.ormi5finalteam1.service.CommentService;
import com.example.ormi5finalteam1.service.EmailService;
import com.example.ormi5finalteam1.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class Ormi5FinalTeam1ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    private CreateUserRequestDto createUserRequestDto;
    private PostDto postDto;
    private CommentDto commentDto;
    private MockHttpSession session;

    @BeforeEach
    public void setUp() {
        createUserRequestDto = new CreateUserRequestDto(
            "testuser@example.com",
            "testUser",
            "password123"
        );

        // 이메일 인증 모킹
        Mockito.when(emailService.isEmailVerified(anyString())).thenReturn(true);

        // 게시글 DTO 초기화
        postDto = new PostDto(1L, 1L, "testUser", "Test Title", "Test Content", 0,
            LocalDateTime.now(), LocalDateTime.now());

        // 댓글 DTO 초기화
        commentDto = new CommentDto(1L, 1L, "testUser", 1L, "Test Title", "Test Comment",
            LocalDateTime.now());

        // 테스트용 세션 생성
        session = new MockHttpSession();
    }

    @Test
    public void testUserAndPostAndCommentOperationsInOrder() throws Exception {
        // 1. 회원가입 테스트
        ResultActions signupResult = mockMvc.perform(post("/api/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequestDto)))
            .andExpect(status().isOk());

        // 2. 로그인 테스트
        MvcResult loginResult = mockMvc.perform(post("/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "testuser@example.com")
                .param("password", "password123")
                .session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(result -> {
                String redirectedUrl = result.getResponse().getRedirectedUrl();
                if (!"/tests".equals(redirectedUrl) && !"/".equals(redirectedUrl)) {
                    throw new AssertionError("Unexpected redirect URL: " + redirectedUrl);
                }
            })
            .andReturn();

        // 로그인 후 세션 업데이트
        session = (MockHttpSession) loginResult.getRequest().getSession();

        // 리다이렉트 URL에 따른 페이지 로드 테스트
        String redirectedUrl = loginResult.getResponse().getRedirectedUrl();
        if ("/tests".equals(redirectedUrl)) {
            // 새 사용자의 경우 (grade가 null)
            mockMvc.perform(get("/tests").session(session))
                .andExpect(status().isOk());
        } else {
            // 기존 사용자의 경우 (grade가 설정됨)
            mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk());
        }

        // 3. 사용자 정보 조회 테스트
        ResultActions getUserResult = mockMvc.perform(get("/api/me")
            .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("testuser@example.com"))
            .andExpect(jsonPath("$.nickname").value("testUser"));

        // 4. 회원 정보 업데이트 테스트
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto("newNickname",
            "newPassword123");

        ResultActions updateResult = mockMvc.perform(put("/api/me")
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequestDto)))
            .andExpect(status().isOk());

        // 5. 수정된 회원 정보 확인 테스트
        ResultActions getUpdatedUserResult = mockMvc.perform(get("/api/me")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value("newNickname"));

        // 6. 게시글 작성 테스트
        Mockito.when(postService.createPost(any(PostDto.class), any())).thenReturn(postDto);

        ResultActions createPostResult = mockMvc.perform(post("/api/posts")
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Test Title"))
            .andExpect(jsonPath("$.content").value("Test Content"));

        // 7. 게시글 삭제 테스트
        doNothing().when(postService).deletePost(anyLong(), any());

        ResultActions deletePostResult = mockMvc.perform(delete("/api/posts/1")
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // 8. 댓글 작성 테스트
        Mockito.when(commentService.createComment(any(CommentDto.class), any()))
            .thenReturn(commentDto);

        ResultActions createCommentResult = mockMvc.perform(post("/api/posts/1/comments")
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Test Comment"));

        // 9. 댓글 삭제 테스트
        doNothing().when(commentService).deleteComment(anyLong(), any());

        ResultActions deleteCommentResult = mockMvc.perform(delete("/api/posts/1/comments/1")
                .with(csrf())
                .session(session))
            .andExpect(status().isNoContent());

        // 댓글 삭제 확인
        Mockito.verify(commentService).deleteComment(anyLong(), any());

        // 10. 로그아웃 테스트
        ResultActions logoutResult = mockMvc.perform(post("/logout")
                .with(csrf())
                .session(session))
            .andExpect(status().is3xxRedirection());
    }
}