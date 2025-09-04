package mockit.mockit.domain.learning.domain.repository;

import mockit.mockit.domain.learning.domain.entity.QuizAttempts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptsRepository extends JpaRepository<QuizAttempts, Long> {
}
