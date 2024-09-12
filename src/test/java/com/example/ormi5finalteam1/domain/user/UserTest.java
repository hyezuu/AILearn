package com.example.ormi5finalteam1.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("test@example.com")
            .password("password")
            .nickname("testUser")
            .build();
    }

    @Test
    void updateLevel_테스트() {
        user.changeReadyStatus(false);
        // 사용자를 4레벨로 설정
        for (int i = 0; i < 4; i++) {
            user.addUserPoint(UserLevelConstants.POINTS_PER_LEVEL);
        }
        assertEquals(4, user.getLevel());
        assertFalse(user.isReadyForUpgrade());  // 4레벨에서는 아직 false여야 함

        // 5레벨로 올리기
        user.addUserPoint(UserLevelConstants.POINTS_PER_LEVEL);

        assertEquals(5, user.getLevel());
        assertTrue(user.isReadyForUpgrade());  // 5레벨부터는 true가 되어야 함

        // 6레벨로 올리기
        user.addUserPoint(UserLevelConstants.POINTS_PER_LEVEL);

        assertEquals(6, user.getLevel());
        assertTrue(user.isReadyForUpgrade());  // 여전히 true 상태여야 함

        // isReadyForUpgrade를 false로 설정
        user.changeReadyStatus(false);

        // 7, 8, 9레벨로 올리기
        for (int i = 0; i < 3; i++) {
            user.addUserPoint(UserLevelConstants.POINTS_PER_LEVEL);
        }

        assertEquals(9, user.getLevel());
        assertFalse(user.isReadyForUpgrade());  // 수동으로 설정한 false 상태가 유지되어야 함

        // 10레벨로 올리기 (다음 5의 배수)
        user.addUserPoint(UserLevelConstants.POINTS_PER_LEVEL);

        assertEquals(10, user.getLevel());
        assertTrue(user.isReadyForUpgrade());  // 다시 true가 되어야 함
    }

}