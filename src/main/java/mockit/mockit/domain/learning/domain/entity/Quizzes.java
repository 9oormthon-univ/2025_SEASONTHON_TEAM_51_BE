package mockit.mockit.domain.learning.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mockit.mockit.domain.model.entity.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(name = "Quizzes")
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
}
