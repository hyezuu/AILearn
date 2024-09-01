package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.domain.user.dto.CreateUserRequestDto;
import com.example.ormi5finalteam1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void createUser(CreateUserRequestDto requestDto) {

        if (isDuplicateEmail(requestDto.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (isDuplicateNickname(requestDto.nickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.builder()
            .email(requestDto.email())
            .password(passwordEncoder.encode(requestDto.password()))
            .nickname(requestDto.nickname())
            .build();

        repository.save(user);
    }

    public boolean isDuplicateEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean isDuplicateNickname(String nickname) {
        return repository.existsByNickname(nickname);
    }

    @Transactional
    public void delete(Provider provider) {
        User user = repository.findById(provider.id())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.delete();
    }

    @Override
    @Transactional
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username"));
        if (user.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.USER_DEACTIVATED);
        }
        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.USER_SUSPENDED);
        }
        user.updateLoginTime();
        return user;
    }
}
