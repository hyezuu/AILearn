package com.example.ormi5finalteam1.controller.rest_controller;

import static com.example.ormi5finalteam1.util.TestSecurityContextFactory.authenticatedProvider;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.domain.vocabulary.dto.MyVocabularyListResponseDto;
import com.example.ormi5finalteam1.service.EmailVerificationService;
import com.example.ormi5finalteam1.service.UserService;
import com.example.ormi5finalteam1.service.VocabularyListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(value = UserController.class)
@WithMockUser(username = "test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private VocabularyListService vocabularyListService;
    @MockBean
    private EmailVerificationService emailVerificationService;

    @Test
    void checkEmail_은_해당_email이_존재할_시_true_를_반환한다() throws Exception {
        //given
        String email = "test@test.com";
        when(userService.existByEmail(anyString())).thenReturn(true);
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/email-duplication")
                .param("email", email)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    void checkEmail_은_해당_email이_존재하지_않을_시_false_를_반환한다() throws Exception {
        //given
        String email = "test@test.com";
        when(userService.existByEmail(anyString())).thenReturn(false);
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/email-duplication")
                .param("email", email)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void checkEmail_은_email_파라미터를_누락할_시_400_에러를_반환한다() throws Exception {
        //given
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/email-duplication")
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    void checkEmail_은_유효하지_않은_이메일로_요청_시_400_에러를_반환한다() throws Exception {
        //given
        String email = "invalid-email";
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/email-duplication")
                .with(csrf())
                .param("email", email)
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    void checkNickname_은_해당_nickname이_존재할_시_true_를_반환한다() throws Exception {
        //given
        when(userService.existByNickname(anyString())).thenReturn(true);
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/nickname-duplication")
                .with(csrf())
                .param("nickname", "nickname")
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    void checkEmail_은_해당_nickname이_존재하지_않을_시_false_를_반환한다() throws Exception {
        //given
        when(userService.existByNickname(anyString())).thenReturn(false);
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/nickname-duplication")
                .with(csrf())
                .param("nickname", "newNickname")
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isOk())
            .andExpect(content().string("false"));
    }

    @Test
    void checkNickname_은_nickname_파라미터를_누락할_시_400_에러를_반환한다() throws Exception {
        //given
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/nickname-duplication")
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    void 사용자는_유효한_데이터로_회원가입을_할_수_있다() throws Exception {
        //given
        CreateUserRequestDto requestDto
            = new CreateUserRequestDto("test@email.com", "testNickname", "testPassword");
        //when
        ResultActions actions
            = mockMvc.perform(
            post("/api/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        //then
        actions.andExpect(status().isOk());
        verify(userService).createUser(requestDto);
    }

    @Test
    void 잘못된_형식의_이메일_로_회원가입시_회원가입에_실패한다() throws Exception {
        //given
        CreateUserRequestDto requestDto
            = new CreateUserRequestDto("invalid-email", "testNickname", "testPassword");
        //when
        ResultActions actions
            = mockMvc.perform(
            post("/api/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        //then
        actions.andExpect(status().isBadRequest());
        verify(userService, never()).createUser(requestDto);
    }

    @Test
    void 너무_짧은_닉네임_으로_회원가입시_회원가입에_실패한다() throws Exception {
        //given
        CreateUserRequestDto requestDto
            = new CreateUserRequestDto("test@email.com", "n", "testPassword");
        //when
        ResultActions actions
            = mockMvc.perform(
            post("/api/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        //then
        actions.andExpect(status().isBadRequest());
        verify(userService, never()).createUser(requestDto);
    }

    @Test
    void 너무_짧은_비밀번호_로_회원가입시_회원가입에_실패한다() throws Exception {
        //given
        CreateUserRequestDto requestDto
            = new CreateUserRequestDto("test@email.com", "testNickname", "short");
        //when
        ResultActions actions
            = mockMvc.perform(
            post("/api/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        //then
        actions.andExpect(status().isBadRequest());
        verify(userService, never()).createUser(requestDto);
    }

    @Test
    void 인증된_사용자는_본인의_정보에_접근할_수_있다() throws Exception {
        //given
        Provider provider
            = new Provider(1L, "test@email.com", "testuser", Role.USER, Grade.A1, 10);
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/me")
                .with(csrf())
                .with(authenticatedProvider(provider))
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isOk())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("test@email.com"))
            .andExpect(jsonPath("$.nickname").value("testuser"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.grade").value("A1"))
            .andExpect(jsonPath("$.grammarExampleCount").value(10));
    }

    @Test
    void 인증된_사용자는_회원_탈퇴를_할_수_있다() throws Exception {
        //given
        Provider provider
            = new Provider(1L, "test@email.com", "testuser", Role.USER, Grade.A1, 10);
        //when
        ResultActions actions
            = mockMvc.perform(
            delete("/api/withdrawal")
                .with(csrf())
                .with(authenticatedProvider(provider))
                .accept(MediaType.APPLICATION_JSON));
        //then
        verify(userService).delete(provider);
        actions.andExpect(status().isNoContent());
    }

    @Test
    void getUserVocabularies_는_사용자의_어휘_목록을_반환한다() throws Exception {
        // given
        Provider provider = new Provider(1L, "test@email.com", "testuser", Role.USER, Grade.A1, 10);

        LocalDateTime now = LocalDateTime.now();
        List<MyVocabularyListResponseDto> vocabularyList = List.of(
            new MyVocabularyListResponseDto(1L, "apple", "사과", "I ate an apple.", "A1", now),
            new MyVocabularyListResponseDto(2L, "banana", "바나나", "The monkey likes bananas.", "A2", now)
        );
        Page<MyVocabularyListResponseDto> mockedPage = new PageImpl<>(vocabularyList);
        when(vocabularyListService.getMyVocabularies(eq(provider), any(Pageable.class))).thenReturn(mockedPage);
        // when
        ResultActions actions = mockMvc.perform(
            get("/api/me/vocabulary-list")
                .with(csrf())
                .with(authenticatedProvider(provider))
                .accept(MediaType.APPLICATION_JSON));
        // then
        actions.andExpect(status().isOk())
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
    }

    @Test
    void requestEmailVerification_은_이메일_인증_요청을_보낼_수_있다() throws Exception {
        // given
        String email = "test@email.com";
        // when
        ResultActions actions = mockMvc.perform(
            post("/api/request-verification")
                .param("email", email)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON));
        // then
        actions.andExpect(status().isOk());
        verify(userService).requestEmailVerification(email);
    }

    @Test
    void requestEmailVerification_은_유효하지_않은_이메일로_요청시_400_에러를_반환한다() throws Exception {
        // given
        String invalidEmail = "invalid-email";
        // when
        ResultActions actions = mockMvc.perform(
            post("/api/request-verification")
                .param("email", invalidEmail)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON));
        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    void verifyEmail_은_이메일과_코드로_인증을_완료할_수_있다() throws Exception {
        // given
        String email = "test@email.com";
        String code = "123456";

        // when
        ResultActions actions = mockMvc.perform(
            post("/api/verify-email")
                .param("email", email)
                .param("code", code)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk());
        verify(emailVerificationService).verifyCode(email, code);
    }

    @Test
    void verifyEmail_은_존재하지_않는_이메일로_인증_실패시_NotFound를_반환한다() throws Exception {
        // given
        String email = "nonexistent@email.com";
        String code = "123456";
        doThrow(new BusinessException(ErrorCode.VERIFICATION_CODE_NOT_FOUND))
            .when(emailVerificationService).verifyCode(email, code);

        // when
        ResultActions actions = mockMvc.perform(
            post("/api/verify-email")
                .param("email", email)
                .param("code", code)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Verification code not found"));
        verify(emailVerificationService).verifyCode(email, code);
    }

    @Test
    void verifyEmail_은_코드_불일치시_BadRequest를_반환한다() throws Exception {
        // given
        String email = "test@email.com";
        String wrongCode = "000000";
        doThrow(new BusinessException(ErrorCode.VERIFICATION_CODE_EMAIL_MISMATCH))
            .when(emailVerificationService).verifyCode(email, wrongCode);

        // when
        ResultActions actions = mockMvc.perform(
            post("/api/verify-email")
                .param("email", email)
                .param("code", wrongCode)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Verification code email is mismatch"));
        verify(emailVerificationService).verifyCode(email, wrongCode);
    }

    @Test
    void verifyEmail_은_만료된_코드로_인증_실패시_BadRequest를_반환한다() throws Exception {
        // given
        String email = "test@email.com";
        String expiredCode = "222222";
        doThrow(new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED))
            .when(emailVerificationService).verifyCode(email, expiredCode);

        // when
        ResultActions actions = mockMvc.perform(
            post("/api/verify-email")
                .param("email", email)
                .param("code", expiredCode)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Verification code is expired"));
        verify(emailVerificationService).verifyCode(email, expiredCode);
    }
}