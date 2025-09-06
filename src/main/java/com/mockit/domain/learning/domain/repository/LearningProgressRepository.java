// LearningProgressRepository.java
package com.mockit.domain.learning.domain.repository;

import com.mockit.domain.learning.domain.entity.LearningProgress;
import com.mockit.domain.learning.domain.entity.LearningProgress.LearningStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {
    List<LearningProgress> findByMemberIdOrderByStepIdAsc(Long memberId);
    Optional<LearningProgress> findByMemberIdAndStepId(Long memberId, Long stepId);
    Optional<LearningProgress> findFirstByMemberIdAndStatusOrderByStepIdAsc(Long memberId, LearningStatus status);
}