package com.mockit.domain.learning.domain.repository;

import com.mockit.domain.learning.domain.entity.QuizAttempts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptsRepository extends JpaRepository<QuizAttempts, Long> {
}
