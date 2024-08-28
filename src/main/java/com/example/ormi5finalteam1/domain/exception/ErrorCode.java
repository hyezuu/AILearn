package com.example.ormi5finalteam1.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(404, "User not found"),
    USER_DEACTIVATED(403, "User is deactivated"),
    USER_SUSPENDED(403, "User is suspended"),
    DUPLICATE_EMAIL(409, "Email is already in use"),
    DUPLICATE_NICKNAME(409, "Nickname is already in use");

    private final int status;
    private final String message;
}
