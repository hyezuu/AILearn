package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.test.SubmitRequestDto;
import com.example.ormi5finalteam1.domain.test.SubmitRequestVo;
import com.example.ormi5finalteam1.domain.test.Test;
import com.example.ormi5finalteam1.domain.test.TestQuestionResponseDto;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.TestRepository;
import com.example.ormi5finalteam1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 로직 변경으로 코드 수정 필요한 테스트 다수 발생
 * 추후 수정하도록 하겠습니다
 */
@ExtendWith(MockitoExtension.class)
public class TestServiceTest {

    @Mock
    private TestRepository testRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @InjectMocks
    private TestService testService;

    private List<Test> A2List = new ArrayList<>();
    private List<Test> C2List = new ArrayList<>();

    User user;
    Provider provider;
    Provider newProvider;

    @BeforeEach
    @Transactional
    public void setUp() {

        for(int i = 1; i < 11; i++) {
            A2List.add(new Test(Long.parseLong(String.valueOf(i)), Grade.A2, "question" + i, "answer" + i));
        }

        for(int i = 1; i < 21; i++) {
            C2List.add(new Test(Long.parseLong(String.valueOf(i)), Grade.C2, "question" + i, "answer" + i));
        }

        provider = new Provider(1L, "test@example.com", "nickname", Role.USER, Grade.C1, 1);
      //  newProvider = new Provider(2L, "test2@example.com", "nickname2", Role.USER, null, 1);
        user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("nickname")
                .build();
    }

    /*@DisplayName("레벨테스트 선택 등급 검증 - A1")
    @org.junit.jupiter.api.Test
    void testSelectedGradeOnLevelTests() {
        // given
        Grade testGrade = Grade.A1;
        when(userService.loadUserByUsername("test2@example.com")).thenReturn(user);

        // when & then
        assertThatThrownBy(() -> testService.getLevelTests(testGrade)).isInstanceOf(
                    IndexOutOfBoundsException.class);
    }*/

    @DisplayName("레벨테스트 선택 등급 검증 - C2")
    @org.junit.jupiter.api.Test
    void testSelectedGradeOnLevelTests2() {
        // given
        Grade testGrade = Grade.C2;

        // when & then
        assertThatThrownBy(() -> testService.getLevelTests(testGrade)).isInstanceOf(
                        BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_TAKE_TEST);
    }

    @DisplayName("레벨테스트 반환 테스트")
    @org.junit.jupiter.api.Test
    void testReturnLevelTests() {
        // given
        Grade testGrade = Grade.A2;
        when(testRepository.findByGrade(testGrade)).thenReturn(A2List);

        // when
        List<TestQuestionResponseDto> returnList = testService.getLevelTests(testGrade);

        // then
        assertThat(returnList.size()).isEqualTo(10);
        for (TestQuestionResponseDto testQuestionResponseDto : returnList) {
            assertThat(testQuestionResponseDto.getGrade()).isEqualTo(testGrade);
        }
    }

    /*@DisplayName("승급테스트 반환 테스트")
    @org.junit.jupiter.api.Test
    void testReturnUpgradeTests() {
        // given
        // Mock 설정
        when(userService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(testRepository.findByGrade(Grade.C2)).thenReturn(C2List);

        // when
        List<TestQuestionResponseDto> result = testService.getUpgradeTests(provider);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(C2List.size());
        assertThat(result.get(0).getGrade()).isEqualTo(Grade.C2);

        // 메소드 호출 검증
        verify(userService).loadUserByUsername("test@example.com");
        verify(testRepository).findByGrade(Grade.C2);

    }*/

    @DisplayName("승급테스트 - 승급")
    @org.junit.jupiter.api.Test
    void testUpgrade() {
        // given
        String email = "test@example.com";

        // Mock 설정
        user.changeGrade(Grade.C1);
        when(userService.loadUserByUsername(email)).thenReturn(user);

        SubmitRequestVo vo = new SubmitRequestVo();
        for(long i = 1L; i < 21; i++) {
            vo.add(new SubmitRequestDto(i, "answer" + i));
            when(testRepository.findById(i)).thenReturn(Optional.ofNullable(C2List.get(Integer.parseInt(String.valueOf(i - 1)))));
        }

        // when
        Grade result = testService.submitUpgradeTests(provider, vo);

        // then
        assertThat(result).isEqualTo(Grade.C2);

        // 메소드 호출 검증
        verify(userService).loadUserByUsername(email);
        for(long i = 1L; i < 21; i++) {
            verify(testRepository).findById(i);
        }

    }

    @DisplayName("승급테스트 - 강등")
    @org.junit.jupiter.api.Test
    void testDowngrade() {
        /// given
        String email = "test@example.com";

        // Mock 설정
        user.changeGrade(Grade.C1);
        when(userService.loadUserByUsername(email)).thenReturn(user);

        SubmitRequestVo vo = new SubmitRequestVo();
        for(long i = 1L; i < 21; i++) {
            vo.add(new SubmitRequestDto(i, "WrongAnswer" + i));
            when(testRepository.findById(i)).thenReturn(Optional.ofNullable(C2List.get(Integer.parseInt(String.valueOf(i - 1)))));
        }

        // when
        Grade result = testService.submitUpgradeTests(provider, vo);

        // then
        assertThat(result).isEqualTo(Grade.B2);

        // 메소드 호출 검증
        verify(userService).loadUserByUsername(email);
        for(long i = 1L; i < 21; i++) {
            verify(testRepository).findById(i);
        }

    }

    @DisplayName("승급테스트 - 유지")
    @org.junit.jupiter.api.Test
    void testKeepGrade() {
        /// given
        String email = "test@example.com";

        // Mock 설정
        user.changeGrade(Grade.C1);
        when(userService.loadUserByUsername(email)).thenReturn(user);

        SubmitRequestVo vo = new SubmitRequestVo();
        for(long i = 1L; i < 9; i++) {
            vo.add(new SubmitRequestDto(i, "answer" + i));
            when(testRepository.findById(i)).thenReturn(Optional.ofNullable(C2List.get(Integer.parseInt(String.valueOf(i - 1)))));
        }
        for(long i = 9L; i < 21; i++) {
            vo.add(new SubmitRequestDto(i, "WrongAnswer" + i));
            when(testRepository.findById(i)).thenReturn(Optional.ofNullable(C2List.get(Integer.parseInt(String.valueOf(i - 1)))));
        }

        // when
        Grade result = testService.submitUpgradeTests(provider, vo);

        // then
        assertThat(result).isEqualTo(Grade.C1);

        // 메소드 호출 검증
        verify(userService).loadUserByUsername(email);
        for(long i = 1L; i < 21; i++) {
            verify(testRepository).findById(i);
        }

    }

    /*@DisplayName("레벨테스트 - 통과")
    @org.junit.jupiter.api.Test
    void testSubmitLevelTests() {
        // given
        when(userService.loadUserByUsername("test2@example.com")).thenReturn(user);
        SubmitRequestVo vo = new SubmitRequestVo();
        for(long i = 1L; i < 7; i++) {
            vo.add(new SubmitRequestDto(i, "answer" + i));
            when(testRepository.findById(i)).thenReturn(Optional.ofNullable(A2List.get(Integer.parseInt(String.valueOf(i - 1)))));
        }
        for(long i = 7L; i < 11; i++) {
            vo.add(new SubmitRequestDto(i, "WrongAnswer" + i));
            when(testRepository.findById(i)).thenReturn(Optional.ofNullable(A2List.get(Integer.parseInt(String.valueOf(i - 1)))));
        }

        // when
        Grade result = testService.submitLevelTests(newProvider, Grade.A2, vo);

        // then
        assertThat(result).isEqualTo(Grade.A2);
       assertThat(user.isReadyForUpgrade()).isEqualTo(false);
    }*/

    /*@DisplayName("레벨테스트 - 실패")
    @org.junit.jupiter.api.Test
    void testFailLevelTests() {
        // given
        when(userService.loadUserByUsername("test2@example.com")).thenReturn(user);
        SubmitRequestVo vo = new SubmitRequestVo();
        for(long i = 1L; i < 11; i++) {
            vo.add(new SubmitRequestDto(i, "WrongAnswer" + i));
            when(testRepository.findById(i)).thenReturn(Optional.ofNullable(A2List.get(Integer.parseInt(String.valueOf(i - 1)))));
        }

        // when
        Grade result = testService.submitLevelTests(newProvider, Grade.A2, vo);

        // then
        assertThat(result).isEqualTo(null);
        assertThat(user.isReadyForUpgrade()).isEqualTo(true);
    }*/


    /*@DisplayName("사용자 테스트 가능 여부 검증")
    @org.junit.jupiter.api.Test
    void testIsReadyForUpgrade() {
        // given
        user.changeReadyStatus(false);
        when(userService.loadUserByUsername("test@example.com")).thenReturn(user);

        // when & then
        assertThatThrownBy(() -> testService.getUpgradeTests(provider)).isInstanceOf(
                        BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_TAKE_TEST);
        assertThatThrownBy(() -> testService.submitLevelTests(provider, Grade.A2, null)).isInstanceOf(
                        BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_TAKE_TEST);
        assertThatThrownBy(() -> testService.submitUpgradeTests(provider, null)).isInstanceOf(
                        BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_TAKE_TEST);

    }*/
}
