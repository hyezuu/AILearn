package com.example.ormi5finalteam1.domain.vocabulary.dto;

import com.example.ormi5finalteam1.domain.vocabulary.VocabularyListVocabulary;
import java.time.LocalDateTime;

public record MyVocabularyListResponseDto(
    Long id,
    String word,
    String meaning,
    String exampleSentence,
    String grade,
    LocalDateTime createdAt
){
    public static MyVocabularyListResponseDto from(VocabularyListVocabulary vlv) {
        return new MyVocabularyListResponseDto(
            vlv.getId(),
            vlv.getVocabulary().getWord(),
            vlv.getVocabulary().getMeaning(),
            vlv.getVocabulary().getExampleSentence(),
            vlv.getGrade().name(),
            vlv.getCreatedAt()
        );
    }
}
