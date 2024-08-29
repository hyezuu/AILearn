package com.example.ormi5finalteam1.service;

import com.example.ormi5finalteam1.domain.user.User;
import com.example.ormi5finalteam1.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserPointService {
  private final UserRepository userRepository;

  public void addPointsToUser(Long id, int points) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    user.addUserPoint(points);
    userRepository.save(user);
  }
}
