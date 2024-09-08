package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import com.example.ormi5finalteam1.domain.essay.dto.response.EssayResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.vocabulary.dto.MyVocabularyListResponseDto;
import com.example.ormi5finalteam1.service.EssayService;
import com.example.ormi5finalteam1.service.UserService;
import com.example.ormi5finalteam1.service.VocabularyListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyInfoController {

    private final EssayService essayService;
    private final UserService userService;
    private final VocabularyListService vocabularyListService;

    @GetMapping
    public String me(
            Model model,
            @AuthenticationPrincipal Provider provider,
            @RequestParam(defaultValue = "0") int EssayPage,
            @RequestParam(defaultValue = "5") int EssaySize,
            @RequestParam(defaultValue = "0") int VocaPage,
            @RequestParam(defaultValue = "5") int VocaSize

    ) {
        if(provider==null) return "redirect:/login";
        Pageable pageable = PageRequest.of(VocaPage, VocaSize, Sort.by("createdAt").descending());
        Page<EssayResponseDto> essayResponseDtoPages = essayService.showMyEssays(provider, EssayPage, EssaySize);
        Page<MyVocabularyListResponseDto> vocabularyRsponseDtoPages = vocabularyListService.getMyVocabularies(provider, pageable);
        User user = userService.getUser(provider.id());

        model.addAttribute("user", user);

        if(user.getGrade() == null) {
            model.addAttribute("userGrade", 7);
        } else {
            model.addAttribute("userGrade",user.getGrade().getIndex());
        }

        model.addAttribute("essays", essayResponseDtoPages);
        model.addAttribute("currentEssayPage", EssayPage + 1);
        model.addAttribute("totalEssayPages", essayResponseDtoPages.getTotalPages());
        model.addAttribute("totalEssayItems", essayResponseDtoPages.getTotalElements());
        model.addAttribute("pageSize", EssaySize);

        model.addAttribute("vocabulary", vocabularyRsponseDtoPages);
        model.addAttribute("currentVocaPage", VocaPage + 1);
        model.addAttribute("totalVocaPages", vocabularyRsponseDtoPages.getTotalPages());
        model.addAttribute("totalVocaItems", vocabularyRsponseDtoPages.getTotalElements());

        return "user/my";
    }

    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal Provider provider, Model model) {
        Provider currentUser = userService.getUser(provider.id()).toProvider();
        model.addAttribute("currentUser", currentUser);
        return "user/user-info-edit";
    }

    @GetMapping("/posts")
    public String myPosts() {
        return "user/my-post-list";
    }
}
