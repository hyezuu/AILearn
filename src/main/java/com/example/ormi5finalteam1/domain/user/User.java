package com.example.ormi5finalteam1.domain.user;

import com.example.ormi5finalteam1.domain.BaseEntity;
import com.example.ormi5finalteam1.domain.Grade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Table(name = "users")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, updatable = false)
    private String email;
    @Column(nullable = false, length = 1000)
    private String password;
    @Column(nullable = false, length = 12, unique = true)
    private String nickname;
    @Enumerated(EnumType.STRING)
    private Grade grade;
    @Column(nullable = false)
    private int point;
    @Column(nullable = false)
    private int level;
    @Column(nullable = false)
    private int grammarExampleCount;
    @Column(nullable = false)
    private boolean isReadyForUpgrade = true;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
    private LocalDateTime lastLoginAt;

    public User(Long id) {
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    public String getUsername() {
        return email;
    }


}
