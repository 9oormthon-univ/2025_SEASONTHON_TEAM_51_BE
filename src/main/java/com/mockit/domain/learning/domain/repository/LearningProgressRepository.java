package com.mockit.domain.learning.domain.repository;

import com.mockit.domain.learning.domain.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {
}
