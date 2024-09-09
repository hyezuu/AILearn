package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.essay.dto.request.EssayRequestDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.EssayGuideResponseDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.EssayResponseDto;
import com.example.ormi5finalteam1.domain.essay.dto.response.ReviewedEssaysResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.service.EssayProcessingService;
import com.example.ormi5finalteam1.service.EssayService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.example.ormi5finalteam1.util.TestSecurityContextFactory.authenticatedProvider;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = EssayController.class)
@WithMockUser(username = "test")
public class EssayControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EssayService essayService;

    @MockBean
    private EssayProcessingService essayProcessingService;

    @Test
    void createEssay_는_에세이를_생성하고_201_응답을_반환한다() throws Exception {
        //given
        EssayRequestDto essayRequestDto = new EssayRequestDto(1L, "Test Topic", "Test Content");
        String requestJson = "{\"userId\":1,\"topic\":\"Test Topic\",\"content\":\"Test Content\"}";

        //when & then
        mockMvc.perform(post("/api/essays")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        Mockito.verify(essayService).createEssay(any(EssayRequestDto.class));
    }

    @Test
    void updateEssay_는_에세이를_수정하고_204_응답을_반환한다() throws Exception {
        //given
        EssayRequestDto essayRequestDto = new EssayRequestDto(1L, "Updated Topic", "Updated Content");
        String requestJson = "{\"userId\":1,\"topic\":\"Updated Topic\",\"content\":\"Updated Content\"}";

        //when & then
        mockMvc.perform(put("/api/essays/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNoContent());

        Mockito.verify(essayService).updateEssay(anyLong(), any(EssayRequestDto.class));
    }

    @Test
    void reviewEssay_는_에세이를_첨삭하고_200_응답을_반환한다() throws Exception {
        //given
        ReviewedEssaysResponseDto responseDto = new ReviewedEssaysResponseDto("Original Content", "Reviewed Content");
        Mockito.when(essayProcessingService.processEssay(anyLong(), any())).thenReturn(responseDto);

        //when & then
        mockMvc.perform(put("/api/essays/1/review").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewedContent").value("Reviewed Content"));

        Mockito.verify(essayProcessingService).processEssay(anyLong(), any());
    }

    @Test
    void showEssayGuide_는_에세이_가이드를_조회하고_200_응답을_반환한다() throws Exception {
        //given
        List<EssayGuideResponseDto> guides = Arrays.asList(
                new EssayGuideResponseDto(Grade.A1,"Guide 1"),
                new EssayGuideResponseDto(Grade.A2,"Guide 2")
        );
        Mockito.when(essayService.showEssayGuide()).thenReturn(guides);

        //when & then
        mockMvc.perform(get("/api/essay-guides").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Guide 1"))
                .andExpect(jsonPath("$[1].content").value("Guide 2"));

        Mockito.verify(essayService).showEssayGuide();
    }

    @Test
    void showMyEssays_는_내_에세이를_조회하고_200_응답을_반환한다() throws Exception {
        //given
        Provider provider
                = new Provider(1L, "test@email.com", "testuser", Role.USER, Grade.A1, 10);
        LocalDateTime now = LocalDateTime.now();
        EssayResponseDto essay1 = new EssayResponseDto(1L,"Topic 1", "Content 1", now);
        EssayResponseDto essay2 = new EssayResponseDto(1L,"Topic 2", "Content 2", now);
        PageImpl<EssayResponseDto> page = new PageImpl<>(Arrays.asList(essay1, essay2));

        Mockito.when(essayService.showMyEssays(any(Provider.class), anyInt(), anyInt())).thenReturn(page);

        //when & then
        mockMvc.perform(get("/api/me/essays?page=0&pageSize=3").with(csrf()).with(authenticatedProvider(provider)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].topic").value("Topic 1"))
                .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty()) // Check createdAt is present
                .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty()) // Check createdAt is present
                .andExpect(jsonPath("$.content[1].topic").value("Topic 2"))
                .andExpect(jsonPath("$.content[1].createdAt").isNotEmpty()); // Check createdAt is present

        Mockito.verify(essayService).showMyEssays(any(Provider.class), anyInt(), anyInt());
    }
}
