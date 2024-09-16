package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.repository.UserRepository;
import com.example.ormi5finalteam1.repository.VocabularyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final UserRepository userRepository;
    private final VocabularyRepository vocabularyRepository;

    @Transactional
    public void saveScore(Long userId, int score) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateHighScore(score);
    }

    public int getHighScore(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return user.getHighScore();
    }

    public List<Vocabulary> getRandomWords(int count) {
        return vocabularyRepository.findRandomWords(PageRequest.of(0, count));
    }
}