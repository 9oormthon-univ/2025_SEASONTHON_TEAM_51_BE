package com.mockit.domain.learning.domain.entity;

import com.mockit.domain.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
public class Quizzes extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false)
    private BigDecimal rewardCapital;

    @Column(nullable = false)
    private Long stepId; // 소속 학습 단계
}