package com.example.ormi5finalteam1;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.comment.dto.CommentDto;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.domain.user.dto.UpdateUserRequestDto;
import com.example.ormi5finalteam1.domain.vocabulary.dto.MyVocabularyListResponseDto;
import com.example.ormi5finalteam1.service.CommentService;
import com.example.ormi5finalteam1.service.EmailService;
import com.example.ormi5finalteam1.service.PostService;
import com.example.ormi5finalteam1.service.VocabularyListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.ormi5finalteam1.util.TestSecurityContextFactory.authenticatedProvider;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @MockBean
    private VocabularyListService vocabularyListService;

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
        verify(commentService).deleteComment(anyLong(), any());

        // 10. 로그아웃 테스트
        ResultActions logoutResult = mockMvc.perform(post("/logout")
                .with(csrf())
                .session(session))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testVocabularyListScenario() throws Exception {
        // Provider 객체 생성
        Provider provider = new Provider(1L, "testuser@example.com", "testuser", Role.USER, Grade.A1, 10);

        // 1. 단어장 생성
        doNothing().when(vocabularyListService).create(any(Provider.class));

        ResultActions createVocabularyListResult = mockMvc.perform(post("/api/vocabulary-list")
                .with(csrf())
                .with(authenticatedProvider(provider)))
            .andExpect(status().isOk());

        // 2. 단어장에 단어 추가
        doNothing().when(vocabularyListService).addVocabulary(any(Provider.class));

        ResultActions addVocabularyResult = mockMvc.perform(post("/api/vocabulary-list/me/vocabularies")
                .with(csrf())
                .with(authenticatedProvider(provider)))
            .andExpect(status().isOk());

        // 3. 내 단어장 단어 조회
        LocalDateTime now = LocalDateTime.now();
        List<MyVocabularyListResponseDto> vocabularyList = List.of(
            new MyVocabularyListResponseDto(1L, "apple", "사과", "I ate an apple.", "A1", now),
            new MyVocabularyListResponseDto(2L, "banana", "바나나", "The monkey likes bananas.", "A2", now)
        );
        Page<MyVocabularyListResponseDto> mockedPage = new PageImpl<>(vocabularyList);

        when(vocabularyListService.getMyVocabularies(any(Provider.class), any(Pageable.class))).thenReturn(mockedPage);

        ResultActions getMyVocabulariesResult = mockMvc.perform(get("/api/me/vocabulary-list")
                .with(csrf())
                .with(authenticatedProvider(provider))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].word").value("apple"))
            .andExpect(jsonPath("$.content[0].meaning").value("사과"))
            .andExpect(jsonPath("$.content[0].exampleSentence").value("I ate an apple."))
            .andExpect(jsonPath("$.content[0].grade").value("A1"))
            .andExpect(jsonPath("$.content[0].createdAt").exists())
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.content[1].word").value("banana"))
            .andExpect(jsonPath("$.content[1].meaning").value("바나나"))
            .andExpect(jsonPath("$.content[1].exampleSentence").value("The monkey likes bananas."))
            .andExpect(jsonPath("$.content[1].grade").value("A2"))
            .andExpect(jsonPath("$.content[1].createdAt").exists());

        // 4. 특정 단어 삭제
        doNothing().when(vocabularyListService).delete(any(Provider.class), anyLong());

        ResultActions deleteVocabularyResult = mockMvc.perform(delete("/api/vocabulary-list/me/vocabularies/1")
                .with(csrf())
                .with(authenticatedProvider(provider)))
            .andExpect(status().isOk());

        // 5. 단어 삭제 후 다시 조회
        List<MyVocabularyListResponseDto> updatedVocabularyList = List.of(
            new MyVocabularyListResponseDto(2L, "banana", "바나나", "The monkey likes bananas.", "A2", now)
        );
        Page<MyVocabularyListResponseDto> updatedMockedPage = new PageImpl<>(updatedVocabularyList);

        when(vocabularyListService.getMyVocabularies(any(Provider.class), any(Pageable.class))).thenReturn(updatedMockedPage);

        ResultActions finalGetResult = mockMvc.perform(get("/api/me/vocabulary-list")
                .with(csrf())
                .with(authenticatedProvider(provider))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].id").value(2))
            .andExpect(jsonPath("$.content[0].word").value("banana"))
            .andExpect(jsonPath("$.content[0].meaning").value("바나나"));

        // 서비스 메소드 호출 확인
        verify(vocabularyListService).create(any(Provider.class));
        verify(vocabularyListService).addVocabulary(any(Provider.class));
        verify(vocabularyListService, times(2)).getMyVocabularies(any(Provider.class), any(Pageable.class));
        verify(vocabularyListService).delete(any(Provider.class), eq(1L));
    }
}