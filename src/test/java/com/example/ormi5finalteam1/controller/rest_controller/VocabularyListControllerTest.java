package com.example.ormi5finalteam1.controller.rest_controller;

import static com.example.ormi5finalteam1.util.TestSecurityContextFactory.authenticatedProvider;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.service.VocabularyListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(VocabularyListController.class)
@WithMockUser(username = "test")
class VocabularyListControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private VocabularyListService vocabularyListService;

    @Test
    void create_는_유저의_단어장_생성_서비스_메서드를_호출한다() throws Exception {
        //given
        Provider provider
            = new Provider(1L, "test@email.com", "testuser", Role.USER, Grade.A1, 10);
        //when
        ResultActions actions
            = mockMvc.perform(
                post("/api/vocabulary-list")
                    .with(csrf())
                    .with(authenticatedProvider(provider)));
        //then
        verify(vocabularyListService).create(provider);
        actions.andExpect(status().isOk());
    }

    @Test
    void addVocabularies_는_유저의_단어장_에_단어를_추가하는_서비스_메서드를_호출한다() throws Exception {
        //given
        Provider provider
            = new Provider(1L, "test@email.com", "testuser", Role.USER, Grade.A1, 10);
        //when
        ResultActions actions
            = mockMvc.perform(
            post("/api/vocabulary-list/me/vocabularies")
                .with(csrf())
                .with(authenticatedProvider(provider)));
        //then
        verify(vocabularyListService).addVocabulary(provider);
        actions.andExpect(status().isOk());
    }

    @Test
    void deleteVocabulary_는_유저의_단어장을_삭제하는_서비스_메서드를_호출한다() throws Exception {
        // given
        Provider provider = new Provider(1L, "test@email.com", "testuser", Role.USER, Grade.A1, 10);
        long vocabularyId = 1L;
        // when
        ResultActions actions = mockMvc.perform(
            delete("/api/vocabulary-list/me/vocabularies/{id}", vocabularyId)
                .with(csrf())
                .with(authenticatedProvider(provider))
        );
        // then
        verify(vocabularyListService).delete(provider, vocabularyId);
        actions.andExpect(status().isOk());
    }

}