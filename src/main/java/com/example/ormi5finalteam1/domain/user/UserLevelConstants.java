package com.example.ormi5finalteam1.domain.user;

public final class UserLevelConstants {
    private UserLevelConstants() {}

    // 경험치 관련 상수
    public static final int EXP_ATTENDANCE = 1;          // 출석
    public static final int EXP_ESSAY_WRITE_AND_REVIEW = 3;  // 에세이 작성 및 첨삭
    public static final int EXP_GRAMMAR_PROBLEM_CORRECT = 1; // 문법 예문 문제 정답
    public static final int EXP_WORD_ADD = 1;            // 단어장에 단어 추가

    // 레벨업 관련 상수
    public static final int POINTS_PER_LEVEL = 10;
    public static final int LEVELS_FOR_UPGRADE_READY = 5;

}
