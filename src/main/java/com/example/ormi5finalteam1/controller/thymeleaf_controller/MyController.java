package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import com.example.ormi5finalteam1.domain.essay.dto.response.EssayResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.service.EssayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyController {

    private final EssayService essayService;

    @GetMapping
    public String me(
            Model model,
            @AuthenticationPrincipal Provider provider,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        if(provider==null) return "redirect:/login";
        Page<EssayResponseDto> essayResponseDtoPages = essayService.showMyEssays(provider, page, size);
        model.addAttribute("user", provider);
        model.addAttribute("essays", essayResponseDtoPages);
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", essayResponseDtoPages.getTotalPages());
        model.addAttribute("totalItems", essayResponseDtoPages.getTotalElements());
        model.addAttribute("pageSize", size);
        return "user/my";
    }
}
