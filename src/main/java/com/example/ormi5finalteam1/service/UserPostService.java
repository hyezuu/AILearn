package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserPostService {
    private final UserRepository userRepository;

    public UserPostService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found. id=" + userId));
    }
}
