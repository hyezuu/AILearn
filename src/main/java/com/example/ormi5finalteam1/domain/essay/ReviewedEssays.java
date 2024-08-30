package com.example.ormi5finalteam1.domain.essay;

import com.example.ormi5finalteam1.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewedEssays extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "essay_id", nullable = false)
    private Essay essay;

    @Column(nullable = false, length = 5000)
    private String content;
}
