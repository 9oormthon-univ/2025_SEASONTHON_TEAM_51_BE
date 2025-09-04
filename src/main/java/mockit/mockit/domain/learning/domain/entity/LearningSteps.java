package mockit.mockit.domain.learning.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mockit.mockit.domain.model.entity.BaseEntity;

@Entity
@Table(name = "LearningSteps")
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
}
