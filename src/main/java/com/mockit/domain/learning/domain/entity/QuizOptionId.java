// src/main/java/com/mockit/domain/learning/domain/entity/QuizOptionId.java
package com.mockit.domain.learning.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class QuizOptionId implements Serializable {

    @Column(name = "quiz_id")
    private Long quizId;

    @Column(name = "option_no")
    private Integer optionNo;   // ✅ getOptionNo() 생김
}