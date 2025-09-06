// src/main/java/com/mockit/domain/learning/domain/entity/QuizOptions.java
package com.mockit.domain.learning.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "quiz_options")
@Getter
@Setter
public class QuizOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long optionId;              // ✅ getOptionId() 생성됨

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "option_text", nullable = false)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "option_no", nullable = false)
    private Integer optionNo;           // ✅ 1,2,3,4… (퀴즈별 번호)
}