package com.mockit.domain.learning.domain.entity;

import com.mockit.domain.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "learning_steps")
@Getter
@Setter
public class LearningSteps extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stepTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "JSON")
    private String ruleJson;

    @Column // nullable OK: 마지막 단계면 null
    private Long nextStepId;
}
