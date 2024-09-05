package com.example.ormi5finalteam1.domain;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Grade {
    A1("Bronze", 0),
    A2("Silver", 1),
    B1("Gold", 2),
    B2("Platinum", 3),
    C1("Diamond", 4),
    C2("Challenger", 5);

    private final String tier;
    private final int index;

    Grade(String tier, int index) {
        this.tier = tier;
        this.index = index;
    }

    public static String[] getGrades() {
        return Arrays.stream(Grade.values())
            .map(Enum::name)
            .toArray(String[]::new);
    }

}
