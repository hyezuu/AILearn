package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.SubmitRequestVo;
import com.example.ormi5finalteam1.domain.test.TestQuestionResponseDto;
import com.example.ormi5finalteam1.domain.test.TestResultResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.service.TestService;
import com.example.ormi5finalteam1.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = TestController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestService testService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Provider provider;
    private User user;
    @BeforeEach
    public void setUp() {
        user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("nick").build();
        user.changeGrade(Grade.A2);
    }


    @DisplayName("레벨테스트 조회")
    @org.junit.jupiter.api.Test
    void testGetLevelTests() throws Exception {
        // given
        provider = new Provider(1L, "test@example.com", "Test User", Role.USER, null, 1);

        Authentication auth = new UsernamePasswordAuthenticationToken(provider, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        TestQuestionResponseDto dto1 = new TestQuestionResponseDto(Grade.B1, 1L, "Question1");
        TestQuestionResponseDto dto2 = new TestQuestionResponseDto(Grade.B1, 2L, "Question2");
        List<TestQuestionResponseDto> responseList = List.of(dto1, dto2);
        when(testService.getLevelTests(Grade.B1)).thenReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/level-tests")
                        .param("grade", Grade.B1.name())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].testId", is(1)))
                .andExpect(jsonPath("$[0].question", is("Question1")))
                .andExpect(jsonPath("$[1].testId", is(2)))
                .andExpect(jsonPath("$[1].question", is("Question2")));
    }

    @DisplayName("레벨테스트 조회 - 사용자 정보 없음")
    @org.junit.jupiter.api.Test
    void testGetLevelTestsWithNoProvider() throws Exception {

        // when & then
        mockMvc.perform(get("/api/level-tests")
                        .param("grade", Grade.B1.name())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("승급테스트 조회")
    @org.junit.jupiter.api.Test
    void testGetUpgradeTests() throws Exception {

        // given
        provider = new Provider(1L, "test@example.com", "Test User", Role.USER, Grade.B1, 1);

        Authentication auth = new UsernamePasswordAuthenticationToken(provider, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        TestQuestionResponseDto dto1 = new TestQuestionResponseDto(Grade.B2, 1L, "Question1");
        TestQuestionResponseDto dto2 = new TestQuestionResponseDto(Grade.B2, 2L, "Question2");
        List<TestQuestionResponseDto> responseList = List.of(dto1, dto2);
        when(userService.loadUserByUsername(provider.email())).thenReturn(user);
        when(testService.getUpgradeTests(user)).thenReturn(responseList);

        // when & then
        mockMvc.perform(get("/api/upgrade-tests")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].testId", is(1)))
                .andExpect(jsonPath("$[0].question", is("Question1")))
                .andExpect(jsonPath("$[1].testId", is(2)))
                .andExpect(jsonPath("$[1].question", is("Question2")));
    }

    @DisplayName("승급테스트 조회 - 사용자 정보 없음")
    @org.junit.jupiter.api.Test
    void testGetUpgradeTestsWithNoProvider() throws Exception {

        // when & then
        mockMvc.perform(get("/api/upgrade-tests")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("레벨테스트 제출 - 통과")
    @org.junit.jupiter.api.Test
    void testSuccessLevelTest() throws Exception {

        // given
        provider = new Provider(1L, "test@example.com", "Test User", Role.USER, null, 1);

        Authentication auth = new UsernamePasswordAuthenticationToken(provider, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        when(testService.submitLevelTests(any(), any(), any())).thenReturn(Grade.B1);

        // when & then
        mockMvc.perform(post("/api/grade")
                        .param("grade", Grade.B1.name())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitRequestVo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Grade.B1.name()));

        verify(testService).submitLevelTests(any(), any(), any());
    }

    @DisplayName("레벨테스트 제출 - 실패")
    @org.junit.jupiter.api.Test
    void testFailedLevelTest() throws Exception {

        // given
        provider = new Provider(1L, "test@example.com", "Test User", Role.USER, null, 1);

        Authentication auth = new UsernamePasswordAuthenticationToken(provider, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        when(testService.submitLevelTests(any(), any(), any())).thenReturn(null);

        // when & then
        mockMvc.perform(post("/api/grade")
                        .param("grade", Grade.B1.name())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitRequestVo())))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(testService).submitLevelTests(any(), any(), any());
    }

    @DisplayName("승급테스트 제출 - 승급")
    @org.junit.jupiter.api.Test
    void testSuccessUpgradeTest() throws Exception {

        // given
        provider = new Provider(1L, "test@example.com", "Test User", Role.USER, Grade.B1, 1);

        Authentication auth = new UsernamePasswordAuthenticationToken(provider, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(testService.renewalSubmitUpgradeTests(any(), any())).thenReturn(new TestResultResponseDto("success", Grade.B2, 91));

        // when & then
        mockMvc.perform(post("/api/upgrade")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitRequestVo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("success"))
                .andExpect(jsonPath("grade").value(Grade.B2.name()))
                .andExpect(jsonPath("score").value(91));
    }

    @DisplayName("승급테스트 제출 - 유지")
    @org.junit.jupiter.api.Test
    void testKeepUpgradeTest() throws Exception {

        // given
        provider = new Provider(1L, "test@example.com", "Test User", Role.USER, Grade.B1, 1);

        Authentication auth = new UsernamePasswordAuthenticationToken(provider, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(testService.renewalSubmitUpgradeTests(any(), any())).thenReturn(new TestResultResponseDto("keep", Grade.B1, 55));

        // when & then
        mockMvc.perform(post("/api/upgrade")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitRequestVo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("keep"))
                .andExpect(jsonPath("grade").value(Grade.B1.name()))
                .andExpect(jsonPath("score").value(55));

        verify(testService).renewalSubmitUpgradeTests(any(), any());
    }

    @DisplayName("승급테스트 제출 - 강등")
    @org.junit.jupiter.api.Test
    void testFailedUpgradeTest() throws Exception {

        // given
        provider = new Provider(1L, "test@example.com", "Test User", Role.USER, Grade.B1, 1);

        Authentication auth = new UsernamePasswordAuthenticationToken(provider, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        when(userService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(testService.renewalSubmitUpgradeTests(any(), any())).thenReturn(new TestResultResponseDto("fail", Grade.A2, 20));

        // when & then
        mockMvc.perform(post("/api/upgrade")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitRequestVo())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("fail"))
                .andExpect(jsonPath("grade").value(Grade.A2.name()))
                .andExpect(jsonPath("score").value(20));

        verify(testService).renewalSubmitUpgradeTests(any(), any());
    }
}
