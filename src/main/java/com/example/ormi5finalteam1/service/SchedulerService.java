package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.Test;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import com.example.ormi5finalteam1.external.api.service.AlanAIService;
import com.example.ormi5finalteam1.external.api.util.ContentParser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final AlanAIService alanAIService;
    private final VocabularyService vocabularyService;
    private final TestService testService;
    private final ContentParser contentParser;

    @Async
    @Scheduled(cron = "10 38 19 * * *")
    public void scheduleApiCallAndParse() {
        for (String gradeStr : Grade.getGrades()) {
            try {
                Grade grade = Grade.valueOf(gradeStr);
                String content = alanAIService.getVocabularyResponseForGrade(gradeStr);

                List<Vocabulary> vocabularies = contentParser.parseVocabularies(content, grade);
                List<Test> tests = contentParser.parseTests(content, grade);

                vocabularyService.saveVocabularies(vocabularies);
                testService.saveTests(tests);

                log.info("Processed {} vocabularies and {} tests for grade {}",
                    vocabularies.size(), tests.size(), gradeStr);
            } catch (Exception e) {
                log.error("Error processing grade {}: ", gradeStr, e);
            }
        }
    }
}
