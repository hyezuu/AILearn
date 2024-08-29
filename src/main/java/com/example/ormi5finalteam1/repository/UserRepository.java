package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);

  Boolean existsByNickname(String nickname);

  Optional<User> findById(Long id);
}
