package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.vocabulary.VocabularyList;
import com.example.ormi5finalteam1.repository.VocabularyListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VocabularyListService {

    private final VocabularyListRepository vocabularyListRepository;

    public void create(Provider provider){
        vocabularyListRepository.save(new VocabularyList(new User(provider.id())));
    }
}
