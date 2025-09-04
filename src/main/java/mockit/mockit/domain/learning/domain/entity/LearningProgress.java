package mockit.mockit.domain.learning.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mockit.mockit.domain.model.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "LearningProgress")
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
