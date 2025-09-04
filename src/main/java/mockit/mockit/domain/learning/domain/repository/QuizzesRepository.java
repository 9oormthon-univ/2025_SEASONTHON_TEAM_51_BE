package mockit.mockit.domain.learning.domain.repository;

import mockit.mockit.domain.learning.domain.entity.Quizzes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizzesRepository extends JpaRepository<Quizzes, Long> {
}
