package com.example.ormi5finalteam1.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_은_유저를_생성할_수_있다() {
        //given
        CreateUserRequestDto requestDto
            = new CreateUserRequestDto("test@test.com", "nickname", "password");
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(repository.existsByNickname(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        //when & then
        assertThatCode(() -> userService.createUser(requestDto)).doesNotThrowAnyException();
    }

    @Test
    void createUser_은_중복된_email_이_존재할_시_예외를_던진다() {
        //given
        CreateUserRequestDto requestDto
            = new CreateUserRequestDto("test@test.com", "nickname", "password");
        when(repository.existsByEmail(anyString())).thenReturn(true);
        //when & then
        assertThatThrownBy(() -> userService.createUser(requestDto)).isInstanceOf(
                BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    void createUser_은_중복된_nickname_이_존재할_시_예외를_던진다() {
        //given
        CreateUserRequestDto requestDto
            = new CreateUserRequestDto("test@test.com", "nickname", "password");
        when(repository.existsByNickname(anyString())).thenReturn(false);
        when(repository.existsByNickname(anyString())).thenReturn(true);
        //when & then
        assertThatThrownBy(() -> userService.createUser(requestDto)).isInstanceOf(
                BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_NICKNAME);
    }

    @Test
    void isDuplicateEmail_은_해당_email_이_있으면_true_를_반환한다() {
        //given
        when(repository.existsByEmail(anyString())).thenReturn(true);
        //when
        boolean result = userService.isDuplicateEmail("test@test.com");
        //then
        assertThat(result).isTrue();
    }

    @Test
    void isDuplicateEmail_은_해당_email_이_없으면_false_를_반환한다() {
        //given
        when(repository.existsByEmail(anyString())).thenReturn(false);
        //when
        boolean result = userService.isDuplicateEmail("test@test.com");
        //then
        assertThat(result).isFalse();
    }

    @Test
    void isDuplicateEmail_은_해당_nickname_이_있으면_true_를_반환한다() {
        //given
        when(repository.existsByNickname(anyString())).thenReturn(true);
        //when
        boolean result = userService.isDuplicateNickname("test");
        //then
        assertThat(result).isTrue();
    }

    @Test
    void isDuplicateEmail_은_해당_nickname_이_없으면_true_를_반환한다() {
        //given
        when(repository.existsByNickname(anyString())).thenReturn(true);
        //when
        boolean result = userService.isDuplicateNickname("test");
        //then
        assertThat(result).isTrue();
    }

}