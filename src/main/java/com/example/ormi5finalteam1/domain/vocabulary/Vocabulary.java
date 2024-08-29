package com.example.ormi5finalteam1.domain.vocabulary;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "vocabularies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vocabulary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private String meaning;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade;
}
