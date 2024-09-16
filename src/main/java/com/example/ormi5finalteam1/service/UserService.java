package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.domain.user.dto.GetTop5UserByScore;
import com.example.ormi5finalteam1.domain.user.dto.UpdateUserRequestDto;
import com.example.ormi5finalteam1.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Transactional
    public void createUser(CreateUserRequestDto requestDto) {

        if (!emailService.isEmailVerified(requestDto.email())) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        if (existByNickname(requestDto.nickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.builder()
            .email(requestDto.email())
            .password(passwordEncoder.encode(requestDto.password()))
            .nickname(requestDto.nickname())
            .build();

        repository.save(user);
        emailService.clearVerificationStatus(requestDto.email());
    }

    @Transactional
    public void requestEmailVerification(String email) throws MessagingException {
        if (existByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        emailService.sendVerificationEmail(email);
    }

    public boolean existByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean existByNickname(String nickname) {
        return repository.existsByNickname(nickname);
    }

    @Transactional
    public void delete(Provider provider) {
        User user = getUser(provider.id());
        user.delete();
    }

    @Transactional(readOnly = true)
    public User getUser(long id) {
        return repository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (user.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.USER_DEACTIVATED);
        }
        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.USER_SUSPENDED);
        }
        user.checkAndAddAttendancePoint();
        user.updateLoginTime();
        return user;
    }

    @Transactional
    public void sendTemporaryPassword(String email) throws MessagingException {
        User user = repository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String tempPassword = generateTemporaryPassword();
        user.updatePassword(passwordEncoder.encode(tempPassword));
        emailService.sendTemporaryPasswordEmail(email, tempPassword);
    }

    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

    @Transactional
    public void updateUser(long id, @Valid UpdateUserRequestDto requestDto) {
        User user = getUser(id);

        if (!user.getNickname().equals(requestDto.nickname())) {
            if (existByNickname(requestDto.nickname())) {
                throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
            }
            user.updateNickname(requestDto.nickname());
        }

        if (requestDto.password() != null && !requestDto.password().isEmpty()) {
            user.updatePassword(passwordEncoder.encode(requestDto.password()));
        }
    }

    public List<GetTop5UserByScore> getTop5UserByScore() {
        return userRepository
            .findTop5ByOrderByHighScoreDescPlayedAtAsc(PageRequest.of(0, 5))
            .stream().map(GetTop5UserByScore::from).toList();
    }
}
