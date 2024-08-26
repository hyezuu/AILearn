package com.example.ormi5finalteam1.domain;

import lombok.Getter;

@Getter
public enum Grade {
    A1("Bronze"),
    A2("Silver"),
    B1("Gold"),
    B2("Platinum"),
    C1("Diamond"),
    C2("Challenger");

    private final String tier;

    Grade(String tier) {
        this.tier = tier;
    }

}
