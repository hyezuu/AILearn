package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.repository.VocabularyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    @Transactional
    public void saveVocabularies(List<Vocabulary> vocabularies) {
        vocabularies.removeIf(vocabulary -> vocabularyRepository.existsByWord(vocabulary.getWord()));
        vocabularyRepository.saveAll(vocabularies);
        log.info("Saved {} new vocabularies", vocabularies.size());
    }
}
