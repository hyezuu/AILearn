package com.example.ormi5finalteam1.controller.rest_controller;

import com.example.ormi5finalteam1.domain.essay.dto.EssayRequestDto;
import com.example.ormi5finalteam1.service.EssayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EssayController {

    private final EssayService essayService;

    @PostMapping("/api/essays")
    public void createEssay(@Valid @RequestBody EssayRequestDto essayRequestDto) {
        essayService.createEssay(essayRequestDto);
    }

}
