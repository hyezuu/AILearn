package com.example.ormi5finalteam1.external.api.util;

import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.Test;
import com.example.ormi5finalteam1.domain.vocabulary.Vocabulary;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ContentParser {

    private static final String VOCABULARY_REGEX =
        "word: (\\w+)\\n" +
            "meaning: (.+?)\\n" +
            "exampleSentence: (.+?)\\n" +
            "question: (.+)";

    public String parseGrammarExamplesQueryResponse(String contentValue) {
        // "INSERT INTO"로 시작하는 부분의 인덱스를 찾음
        int startIndex = contentValue.indexOf("INSERT INTO");

        // 마지막 세미콜론의 인덱스를 찾음
        int endIndex = contentValue.lastIndexOf(";") + 1;

        // 쿼리를 추출하여 반환
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return contentValue.substring(startIndex, endIndex);
        } else {
            return null;  // INSERT 쿼리를 찾지 못한 경우
        }
    }

    public List<Vocabulary> parseVocabularies(String content, Grade grade) {
        List<Vocabulary> vocabularies = new ArrayList<>();
        Pattern pattern = Pattern.compile(VOCABULARY_REGEX);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String word = matcher.group(1);
            String meaning = matcher.group(2);
            String exampleSentence = matcher.group(3);
            vocabularies.add(new Vocabulary(word, meaning, grade, exampleSentence));
        }
        return vocabularies;
    }

    public List<Test> parseTests(String content, Grade grade) {
        List<Test> tests = new ArrayList<>();
        Pattern pattern = Pattern.compile(VOCABULARY_REGEX);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String word = matcher.group(1);
            String question = matcher.group(4);
            tests.add(new Test(grade, question, word));
        }
        return tests;
    }

    public String parseEssayReviewResponse(String contentValue) {
        int startIndex = contentValue.lastIndexOf("'content': ") + 12; // 마지막 'content': 에 답변이 존재함
        int endIndex = contentValue.length() - 7; // 마지막 괄호들 제거
        String parsedContent = contentValue.substring(startIndex,endIndex);
        parsedContent = parsedContent.replace("\\n", "");
        parsedContent = parsedContent.replace("\\\"", "\"");
        return parsedContent;
    }

}
