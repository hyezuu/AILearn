package com.example.ormi5finalteam1.domain.user;

import com.example.ormi5finalteam1.domain.Grade;

public record Provider(long id, String email, String nickname, Role role, Grade grade, int grammarExampleCount) {
}
