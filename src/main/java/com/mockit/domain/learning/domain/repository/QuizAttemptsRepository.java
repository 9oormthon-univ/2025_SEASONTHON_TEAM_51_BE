// src/main/java/com/mockit/domain/learning/domain/repository/QuizAttemptsRepository.java
package com.mockit.domain.learning.domain.repository;

import com.mockit.domain.learning.domain.entity.QuizAttempts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizAttemptsRepository extends JpaRepository<QuizAttempts, Long> {

    // ✅ 이 퀴즈를 '이미 맞춘 적'이 있는지(정답 시도 존재 여부)
    boolean existsByMemberIdAndQuizIdAndIsCorrectTrue(Long memberId, Long quizId);

    // (선택) 최근 정답 시도 하나 보고 싶을 때
    Optional<QuizAttempts> findFirstByMemberIdAndQuizIdAndIsCorrectTrue(Long memberId, Long quizId);

    Optional<QuizAttempts> findByMemberIdAndQuizId(Long memberId, Long quizId); // ⬅️ 추가
}