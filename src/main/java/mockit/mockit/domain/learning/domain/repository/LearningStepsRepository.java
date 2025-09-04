package mockit.mockit.domain.learning.domain.repository;

import mockit.mockit.domain.learning.domain.entity.LearningSteps;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningStepsRepository extends JpaRepository<LearningSteps, Long> {
}
