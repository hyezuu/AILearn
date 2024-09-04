package com.example.ormi5finalteam1.domain.essay;

import com.example.ormi5finalteam1.domain.BaseEntity;
import com.example.ormi5finalteam1.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "essays")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Essay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false, length = 5000)
    private String content;

}
