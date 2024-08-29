package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.essay.Essay;
import com.example.ormi5finalteam1.domain.essay.dto.EssayRequestDto;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.EssayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EssayService {

    private final EssayRepository essayRepository;

    public void createEssay(EssayRequestDto essayRequestDto) {
        Essay essay = convertToEntity(essayRequestDto);
        essayRepository.save(essay);
    }

    public void updateEssay(Long id, EssayRequestDto essayRequestDto) {
        Essay essay = essayRepository.findById(id).orElseThrow(() -> new RuntimeException("Essay not found!"));

        Essay updatedEssay = essay.builder()
                .id(id)
                .user(essay.getUser())
                .topic(essayRequestDto.topic())
                .content(essayRequestDto.content())
                .build();
        essayRepository.save(updatedEssay);
    }


    private Essay convertToEntity(EssayRequestDto essayRequestDto) {

      return Essay.builder()
              .user(new User(essayRequestDto.userId()))
              .topic(essayRequestDto.topic())
              .content(essayRequestDto.content())
              .build();
    }
}
