package com.mockit.domain.learning.domain.entity;

import com.mockit.domain.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_progress")
@Getter
@Setter
public class LearningProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long stepId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LearningStatus status;

    private LocalDateTime doneAt;

    public enum LearningStatus { LOCKED, ONGOING, DONE }
}
