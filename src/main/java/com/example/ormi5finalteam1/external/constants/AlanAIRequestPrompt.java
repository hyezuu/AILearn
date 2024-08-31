package com.example.ormi5finalteam1.external.constants;

import lombok.Getter;

@Getter
public enum AlanAIRequestPrompt {
    // 문법 연습용 예문 단계별 문제 + insert query 요청
    GRAMMAR_EXAMPLES_INSERT_QUERY(
        "문법 연습용 예문 단계별 문제 insert query 요청",
        "영어 문법 예문 연습용 문제가 필요한데, {grade}단계 10문항 문제, 해답, 해설을 포함해서 만들고 insert 쿼리로 만들어줘. 테이블명은 "
            + "grammar_examples이고 각 필드는 grade, question, answer, commentary 이렇게 있어"
    ),
    VOCABULARY_DEFAULT_PROMPT(
        "단어 및 단어 문제 응답 요청",
        "일년 전 오늘 날짜의 기사들 중에서 %s 레벨의 영어 단어 15개를 다음 형식으로 정확히 작성해주세요:\n"
            + "\n"
            + "word: [단어]\n"
            + "meaning: [의미]\n"
            + "exampleSentence: [예문]\n"
            + "question: [문제] ([정답], [오답1], [오답2], [오답3])\n"
            + "\n"
            + "각 항목에 대한 지침:\n"
            + "1. [단어]는 한 단어여야 합니다.\n"
            + "2. [의미]는 한국어 뜻을 의미합니다.\n"
            + "3. [예문]은 해당 단어를 포함한 완전한 문장이어야 합니다.\n"
            + "4. [문제]는 예문을 기반으로 하되, 해당 단어 자리에 빈칸(_______)을 넣어주세요.\n"
            + "5. 정답은 반드시 [단어]와 같아야 하며, 오답들은 의미상 예문에 어울리지 않아야 합니다.\n"
            + "\n"
            + "예시:\n"
            + "word: euphoria\n"
            + "meaning: 행복감\n"
            + "exampleSentence: After winning the championship, the team was overwhelmed with euphoria.\n"
            + "question: After winning the championship, the team was overwhelmed with _______. (euphoria, melancholy, anxiety, apathy)\n"
            + "\n"
            + "주의사항:\n"
            + "- 각 항목은 정확히 위의 형식을 따라야 합니다.\n"
            + "- 줄바꿈은 각 항목 사이에만 사용하고, 그 외의 마크다운 문법은 사용하지 마세요.\n"
            + "- 15개의 단어는 모두 새로운 단어여야 하며, 이전에 사용된 단어는 피해주세요.\n"
            + "- 적합한 단어가 없다면 전년도 기사를 참고해도 좋습니다.\n"
            + "- 기사 제목이나 다른 추가 정보는 포함하지 마세요.\n"
            + "- 정답과 오답의 순서는 랜덤으로 바꿔주세요.\n"
            + "이 형식을 정확히 따라 15개의 단어 항목을 생성해주세요."
    );

    private final String description; // 요청의 목적을 설명하는 필드
    private final String promptTemplate; // 실제 프롬프트로 사용될 텍스트 템플릿 필드

    // 생성자
    AlanAIRequestPrompt(String description, String promptTemplate) {
        this.description = description;
        this.promptTemplate = promptTemplate;
    }

    // 변수를 삽입한 프롬프트를 반환하는 메소드
    public String applyVariables(String... variables) {
        String prompt = promptTemplate;
        for (int i = 0; i < variables.length; i++) {
            prompt = prompt.replace("{" + i + "}", variables[i]);
        }
        return prompt;
    }
}
