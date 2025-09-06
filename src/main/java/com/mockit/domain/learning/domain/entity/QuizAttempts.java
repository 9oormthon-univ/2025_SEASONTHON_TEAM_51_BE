package com.mockit.domain.learning.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
@Getter
@Setter
public class QuizAttempts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long quizId;

    private Long selectedOptionId;

    @Column(nullable = false)
    private Boolean isCorrect;

    @Column(nullable = false)
    private Boolean rewarded;

    @Column(nullable = false)
    private LocalDateTime attemptedAt;
}
