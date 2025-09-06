// src/main/java/com/mockit/domain/learning/domain/repository/QuizOptionsRepository.java
package com.mockit.domain.learning.domain.repository;

import com.mockit.domain.learning.domain.entity.QuizOptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizOptionsRepository extends JpaRepository<QuizOptions, Long> {

    // ✅ 퀴즈별 보기 목록을 option_no 오름차순으로
    List<QuizOptions> findByQuizIdOrderByOptionNoAsc(Long quizId);
    // ✅ 항상 option_no 기준으로만 찾음
    Optional<QuizOptions> findByQuizIdAndOptionNo(Long quizId, Integer optionNo);
}