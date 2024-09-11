package com.example.ormi5finalteam1;

import static com.example.ormi5finalteam1.util.TestSecurityContextFactory.authenticatedProvider;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.comment.Comment;
import com.example.ormi5finalteam1.domain.comment.dto.CommentDto;
import com.example.ormi5finalteam1.domain.post.Post;
import com.example.ormi5finalteam1.domain.post.dto.PostDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.domain.user.dto.UpdateUserRequestDto;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.repository.CommentRepository;
import com.example.ormi5finalteam1.repository.PostRepository;
import com.example.ormi5finalteam1.repository.UserRepository;
import com.example.ormi5finalteam1.service.CommentService;
import com.example.ormi5finalteam1.service.EmailService;
import com.example.ormi5finalteam1.service.PostService;
import com.example.ormi5finalteam1.service.VocabularyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class Ormi5FinalTeam1ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private CreateUserRequestDto createUserRequestDto;
    private PostDto postDto;
    private CommentDto commentDto;
    private MockHttpSession session;

    @BeforeEach
    public void setUp() {
        // 테스트 데이터 초기화
        createUserRequestDto = new CreateUserRequestDto(
            "testuser@example.com",
            "testUser",
            "password123"
        );

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
    public void 유저_회원가입_로그인_커뮤니티_기능_테스트() throws Exception {
        // 1. 회원가입 테스트 - 실제 데이터베이스와 상호작용

        when(emailService.isEmailVerified("testuser@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequestDto)))
            .andExpect(status().isOk());

        User user = userRepository.findByEmail("testuser@example.com").orElseThrow();

        Provider provider = user.toProvider();

		// 2. 로그인 테스트
		MvcResult loginResult = mockMvc.perform(post("/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "testuser@example.com")
				.param("password", "password123")
				.session(session))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.redirectUrl").exists())
			.andReturn();

		// 로그인 후 세션 업데이트
		session = (MockHttpSession) loginResult.getRequest().getSession();

		// JSON 응답에서 redirectUrl 추출
		String responseContent = loginResult.getResponse().getContentAsString();
		String redirectUrl = JsonPath.parse(responseContent).read("$.redirectUrl");

		// 리다이렉트 URL에 따른 페이지 로드 테스트
		mockMvc.perform(get(redirectUrl).session(session))
			.andExpect(status().isOk());

        // 3. 사용자 정보 조회 테스트
        mockMvc.perform(get("/api/me")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("testuser@example.com"))
            .andExpect(jsonPath("$.nickname").value("testUser"));

        // 4. 회원 정보 업데이트 테스트
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto("newNickname",
            "newPassword123");

        mockMvc.perform(put("/api/me")
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequestDto)))
            .andExpect(status().isOk());

        // 5. 수정된 회원 정보 확인 테스트
        mockMvc.perform(get("/api/me")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value("newNickname"));

        // 6. 게시글 작성 테스트
        postDto = postService.createPost(postDto, provider);

        mockMvc.perform(post("/api/posts")
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Test Title"))
            .andExpect(jsonPath("$.content").value("Test Content"));

        // 7. 댓글 작성 테스트
        commentDto = commentService.createComment(commentDto, provider);

        mockMvc.perform(post("/api/posts/" + postDto.getId() + "/comments")
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Test Comment"));

        // 8. 댓글 삭제 테스트
        mockMvc.perform(delete("/api/posts/" + postDto.getId() + "/comments/" + commentDto.getId())
                .with(csrf())
                .session(session))
            .andExpect(status().isNoContent());

        Optional<Comment> comment = commentRepository.findById(commentDto.getId());
        assertThat(comment).isEmpty();

        // 9. 게시글 삭제 테스트
        mockMvc.perform(delete("/api/posts/" + postDto.getId())
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        // 소프트 딜리트 확인
        Post post = postRepository.findById(postDto.getId()).orElseThrow();
        assertThat(post.getDeletedAt()).isNotNull();

        // 10. 로그아웃 테스트
        mockMvc.perform(post("/logout")
                .with(csrf())
                .session(session))
            .andExpect(status().is3xxRedirection());
    }


    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    public void 유저_단어장_기능_테스트() throws Exception {

        // 테스트용 사용자 생성
        User user = User.builder()
            .email("test@example.com")
            .password("password")
            .nickname("testUser")
            .build();

        user = userRepository.save(user);
        user.changeGrade(Grade.A1);

        // Provider 객체 초기화
        Provider provider = user.toProvider();

        // 1. 단어장 생성
        mockMvc.perform(post("/api/vocabulary-list")
                .with(csrf())
                .with(authenticatedProvider(provider))
                .session(session))
            .andExpect(status().isOk());

        // 2. 단어장에 단어 추가
        Vocabulary vocabulary1 = new Vocabulary("apple", "사과", Grade.A1, "I ate an apple.");
        Vocabulary vocabulary2 = new Vocabulary("banana", "바나나", Grade.A1,
            "The monkey likes bananas.");

        List<Vocabulary> vocabularies = new ArrayList<>();
        vocabularies.add(vocabulary1);
        vocabularies.add(vocabulary2);

        vocabularyService.saveVocabularies(vocabularies); // Vocabulary 엔티티 저장
        mockMvc.perform(post("/api/vocabulary-list/me/vocabularies")
                .with(csrf())
                .session(session))
            .andExpect(status().isOk());

        // 3. 내 단어장 단어 조회
        mockMvc.perform(get("/api/me/vocabulary-list")
                .with(csrf())
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)));

        // 4. 특정 단어 삭제
        mockMvc.perform(delete("/api/vocabulary-list/me/vocabularies/1")
                .with(csrf())
                .session(session))
            .andExpect(status().isOk());

        // 5. 단어 삭제 후 다시 조회
        mockMvc.perform(get("/api/me/vocabulary-list")
                .with(csrf())
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }
}
