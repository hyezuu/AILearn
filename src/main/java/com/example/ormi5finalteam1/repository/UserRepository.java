package com.example.ormi5finalteam1.repository;

import com.example.ormi5finalteam1.domain.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Boolean existsByEmail(String email);

  Boolean existsByNickname(String nickname);

  Page<User> findAllByOrderByRoleAscId(Pageable pageable);

  Page<User> findByNicknameContaining(String nickname, Pageable pageable);

  List<User> findTop5ByOrderByHighScoreDesc();
}
