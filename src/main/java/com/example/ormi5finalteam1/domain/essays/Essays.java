package com.example.ormi5finalteam1.domain.essays;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "essays")
public class Essays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", columnDefinition = "bigint")
    private Long id;

    @Column(name = "grade", nullable = false)
    private String grade;

    @Column(name = "domain", nullable = false)
    private String domain;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageLevel languageLevel;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 10000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum LanguageLevel {
        A1, A2, B1, B2, C1, C2
    }

}
