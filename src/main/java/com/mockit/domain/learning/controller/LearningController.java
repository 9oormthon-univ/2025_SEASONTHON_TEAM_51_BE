// src/main/java/com/mockit/domain/learning/controller/LearningController.java
package com.mockit.domain.learning.controller;

import com.mockit.domain.learning.dto.LearningDtos.CompleteResponse;
import com.mockit.domain.learning.dto.LearningDtos.QuizAnswerRequest;
import com.mockit.domain.learning.dto.LearningDtos.QuizAnswerResponse;
import com.mockit.domain.learning.dto.LearningDtos.QuizItem;
import com.mockit.domain.learning.dto.LearningDtos.StepsResponse;
import com.mockit.domain.learning.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping // 필요시 여기다 "/api" 등 공통 prefix 넣어도 OK
@RequiredArgsConstructor
public class LearningController {

    private final LearningService learningService;

    // TODO: 시큐리티 연동 전까지 임시로 사용 (테스트용)
    private Long currentMemberId() {
        return 1L;
    }

    /** 1) GET /learning/steps */
    @GetMapping("/learning/steps")
    public ResponseEntity<StepsResponse> getSteps() {
        return ResponseEntity.ok(learningService.getSteps(currentMemberId()));
    }

    /** 2) POST /learning/steps/{id}/complete */
    @PostMapping("/learning/steps/{id}/complete")
    public ResponseEntity<CompleteResponse> completeStep(@PathVariable Long id) {
        return ResponseEntity.ok(learningService.completeStep(currentMemberId(), id));
    }

    /** 3) GET /quizzes/next  (옵션: ?stepId=123) */
    @GetMapping("/quizzes/next")
    public ResponseEntity<?> getNextQuiz(@RequestParam(value = "stepId", required = false) Long stepId) {
        return learningService.getNextQuiz(currentMemberId(), stepId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(
                        QuizAnswerResponse.builder()
                                .isCorrect(false)
                                .message("No pending quizzes.")
                                .build()
                ));
    }

    /** 4) POST /quizzes/{id}/answer  { "optionId": 2 } */
    @PostMapping("/quizzes/{id}/answer")
    public ResponseEntity<QuizAnswerResponse> answerQuiz(
            @PathVariable("id") Long quizId,
            @RequestBody QuizAnswerRequest req   // ✅ 요청 바디에서 optionId 받음
    ) {
        return ResponseEntity.ok(
                learningService.answerQuiz(currentMemberId(), quizId, req.getOptionId())
        );
    }

}