package com.example.ormi5finalteam1.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404, "User not found"),
    USER_DEACTIVATED(403, "User is deactivated"),
    USER_SUSPENDED(403, "User is suspended"),
    DUPLICATE_EMAIL(409, "Email is already in use"),
    DUPLICATE_NICKNAME(409, "Nickname is already in use"),
    GRAMMAR_EXAMPLES_NOT_FOUND(404,"No grammar examples found"),
    ESSAY_NOT_FOUND(404,"Essay not found"),
    TEST_NOT_FOUND(404, "Test not found"),
    CANNOT_TAKE_TEST(400, "Cannot take test");

    private final int status;
    private final String message;
}
