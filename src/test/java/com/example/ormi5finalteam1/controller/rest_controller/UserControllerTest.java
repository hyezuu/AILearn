package com.example.ormi5finalteam1.controller.rest_controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ormi5finalteam1.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    void checkEmail_은_해당_email이_존재할_시_true_를_반환한다() throws Exception {
        //given
        when(userService.isDuplicateEmail(anyString())).thenReturn(true);
        //when
        ResultActions actions
            = mockMvc.perform(
                get("/api/email-duplication")
                    .param("email", "email")
                    .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    void checkEmail_은_해당_email이_존재하지_않을_시_false_를_반환한다() throws Exception {
        //given
        when(userService.isDuplicateEmail(anyString())).thenReturn(false);
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/email-duplication")
                .param("email", "newEmail")
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
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    void checkNickname_은_해당_nickname이_존재할_시_true_를_반환한다() throws Exception {
        //given
        when(userService.isDuplicateNickname(anyString())).thenReturn(true);
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/nickname-duplication")
                .param("nickname", "nickname")
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    void checkEmail_은_해당_nickname이_존재하지_않을_시_false_를_반환한다() throws Exception {
        //given
        when(userService.isDuplicateNickname(anyString())).thenReturn(false);
        //when
        ResultActions actions
            = mockMvc.perform(
            get("/api/nickname-duplication")
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
                .accept(MediaType.APPLICATION_JSON));
        //then
        actions.andExpect(status().isBadRequest());
    }

}