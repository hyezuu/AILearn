package com.example.ormi5finalteam1.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.Grade;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.repository.UserRepository;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private User mockUser;

    @InjectMocks
    private UserService userService;


    @Test
    void createUser_은_유저를_생성할_수_있다() {
        //given
        CreateUserRequestDto requestDto
            = new CreateUserRequestDto("test@test.com", "nickname", "password");
        when(emailVerificationService.isEmailVerified(anyString())).thenReturn(true);
        when(repository.existsByNickname(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        //when & then
        assertThatCode(() -> userService.createUser(requestDto)).doesNotThrowAnyException();
        verify(repository).save(any(User.class));
        verify(emailVerificationService).clearVerificationStatus(anyString());
    }

    @Test
    void createUser_은_이메일이_검증되지_않았을_경우_BusinessException을_던진다() {
        // given
        CreateUserRequestDto requestDto = new CreateUserRequestDto("test@test.com", "nickname", "password");
        when(emailVerificationService.isEmailVerified(anyString())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.createUser(requestDto))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_NOT_VERIFIED);
        verify(repository,never()).save(any(User.class));
        verify(emailVerificationService,never()).clearVerificationStatus(anyString());
    }

    @Test
    void createUser_은_중복된_nickname_이_존재할_시_BusinessException을_던진다() {
        //given
        CreateUserRequestDto requestDto
            = new CreateUserRequestDto("test@test.com", "nickname", "password");
        when(emailVerificationService.isEmailVerified(anyString())).thenReturn(true);
        when(repository.existsByNickname(anyString())).thenReturn(true);
        //when & then
        assertThatThrownBy(() -> userService.createUser(requestDto)).isInstanceOf(
                BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_NICKNAME);
        verify(repository,never()).save(any(User.class));
        verify(emailVerificationService,never()).clearVerificationStatus(anyString());
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
    void requestEmailVerification_은_중복된_email이_없을_때_이메일을_전송한다() throws MessagingException {
        // given
        String email = "test@test.com";
        when(repository.existsByEmail(anyString())).thenReturn(false);

        // when & then
        assertThatCode(() -> userService.requestEmailVerification(email)).doesNotThrowAnyException();
        verify(emailVerificationService).sendVerificationEmail(email);
    }

    @Test
    void requestEmailVerification_은_중복된_email이_있을_때_BusinessException을_던진다() throws MessagingException {
        // given
        String email = "test@test.com";
        when(repository.existsByEmail(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.requestEmailVerification(email))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
        verify(emailVerificationService, never()).sendVerificationEmail(email);
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

    @Test
    void delete_는_유저의_deleted_at_을_설정할_수_있다() {
        //given
        Provider provider
            = new Provider(1L, "test@test.com", "test", Role.USER, Grade.A1, 0);
        User user = new User(1L);
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));
        //when
        userService.delete(provider);
        //then
        assertThat(user.getDeletedAt()).isNotNull();
    }

    @Test
    void getUser_는_유저를_찾을_수_있다() {
        // given
        long userId = 1L;
        User user = new User(userId);
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User result = userService.getUser(userId);

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    void getUser_는_유저가_없을_때_BusinessException을_던진다() {
        // given
        long userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(userId))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void loadUserByUsername_은_email_로_유저_데이터를_찾아올_수_있다() {
        // given
        String email = "test@test.com";
        when(repository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(mockUser.getEmail()).thenReturn(email);
        when(mockUser.isActive()).thenReturn(true);
        when(mockUser.getDeletedAt()).thenReturn(null);

        // when
        User result = userService.loadUserByUsername(email);

        // then
        assertThat(result).isEqualTo(mockUser);
        assertThat(result.getEmail()).isEqualTo(email);
        verify(mockUser).checkAndAddAttendancePoint();
        verify(mockUser).updateLoginTime();
        verify(repository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_은_유저가_존재하지_않는_경우_UsernameNotFoundException_을_던진다() {
        //given
        String email = "test@test.com";
        when(repository.findByEmail(email)).thenReturn(Optional.empty());
        //when & then
        assertThatThrownBy(() -> userService.loadUserByUsername(email))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessage("User not found with username");
    }

    @Test
    void loadUserByUsername_은_유저가_deactive_인_경우_BusinessException_을_던진다() {
        // given
        String email = "inactive@test.com";
        when(repository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(mockUser.isActive()).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.loadUserByUsername(email))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_SUSPENDED);
    }

    @Test
    void loadUserByUsername_은_유저가_deleted_인_경우_BusinessException_을_던진다() {
        // given
        String email = "deleted@test.com";
        when(repository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(mockUser.getDeletedAt()).thenReturn(LocalDateTime.now());

        // when & then
        assertThatThrownBy(() -> userService.loadUserByUsername(email))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_DEACTIVATED);
    }
}