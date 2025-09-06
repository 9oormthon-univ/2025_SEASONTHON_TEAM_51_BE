// src/main/java/com/mockit/domain/learning/service/LearningService.java
package com.mockit.domain.learning.service;

import com.mockit.domain.learning.dto.LearningDtos.*;
import com.mockit.domain.learning.domain.entity.*;
import com.mockit.domain.learning.domain.entity.LearningProgress.LearningStatus;
import com.mockit.domain.learning.domain.repository.*;
import com.mockit.domain.member.domain.entity.PortfolioLedger;
import com.mockit.domain.member.domain.repository.PortfolioLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningService {

    private final LearningStepsRepository stepsRepo;
    private final LearningProgressRepository progRepo;
    private final QuizzesRepository quizzesRepo;
    private final QuizOptionsRepository optionsRepo;
    private final QuizAttemptsRepository attemptsRepo;
    private final PortfolioLedgerRepository ledgerRepo;

    @Transactional(readOnly = true)
    public StepsResponse getSteps(Long memberId) {
        var steps = stepsRepo.findAll().stream()
                .sorted(Comparator.comparing(LearningSteps::getId))
                .toList();

        var progMap = progRepo.findByMemberIdOrderByStepIdAsc(memberId).stream()
                .collect(Collectors.toMap(LearningProgress::getStepId, p -> p));

        List<StepItem> items = new ArrayList<>();
        boolean seenOngoing = false;

        for (int i = 0; i < steps.size(); i++) {
            var s = steps.get(i);
            var p = progMap.get(s.getId());
            String status;

            if (p != null) {
                status = p.getStatus().name();
            } else if (i == 0) {
                status = LearningStatus.ONGOING.name(); // 첫 단계 자동 오픈
            } else {
                boolean prevDone = i > 0 && items.get(i - 1).getStatus().equals(LearningStatus.DONE.name());
                status = (!seenOngoing && prevDone) ? LearningStatus.ONGOING.name() : LearningStatus.LOCKED.name();
            }
            if (status.equals(LearningStatus.ONGOING.name())) seenOngoing = true;

            items.add(StepItem.builder()
                    .id(s.getId())
                    .title(s.getStepTitle())
                    .status(status)
                    .build());
        }
        return StepsResponse.builder().steps(items).build();
    }

    @Transactional
    public CompleteResponse completeStep(Long memberId, Long stepId) {
        var step = stepsRepo.findById(stepId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid step id"));

        var progress = progRepo.findByMemberIdAndStepId(memberId, stepId)
                .orElseGet(() -> {
                    var np = new LearningProgress();
                    np.setMemberId(memberId);
                    np.setStepId(stepId);
                    np.setStatus(LearningStatus.ONGOING);
                    return np;
                });

        if (progress.getStatus() == LearningStatus.DONE) {
            return new CompleteResponse(true, "Already completed.");
        }

        progress.setStatus(LearningStatus.DONE);
        progress.setDoneAt(LocalDateTime.now());
        progRepo.save(progress);

        // 다음 단계 자동 OPEN (존재하면 LOCKED → ONGOING 승격)
        if (step.getNextStepId() != null) {
            var nextId = step.getNextStepId();
            var next = progRepo.findByMemberIdAndStepId(memberId, nextId)
                    .orElseGet(() -> {
                        var np = new LearningProgress();
                        np.setMemberId(memberId);
                        np.setStepId(nextId);
                        np.setStatus(LearningStatus.ONGOING);
                        return np;
                    });

            if (next.getStatus() == null || next.getStatus() == LearningStatus.LOCKED) {
                next.setStatus(LearningStatus.ONGOING);
            }
            progRepo.save(next);
        }
        return new CompleteResponse(true, "Learning step completed.");
    }

    @Transactional(readOnly = true)
    public Optional<QuizItem> getNextQuiz(Long memberId, Long stepIdOrNull) {
        Long stepId = stepIdOrNull;
        if (stepId == null) {
            var ongoing = progRepo.findFirstByMemberIdAndStatusOrderByStepIdAsc(memberId, LearningStatus.ONGOING);
            if (ongoing.isEmpty()) return Optional.empty();
            stepId = ongoing.get().getStepId();
        }

        var quizzes = quizzesRepo.findByStepIdOrderByQuizIdAsc(stepId);
        for (var q : quizzes) {
            // 이미 정답 맞춘 퀴즈는 스킵 (오답만 있는 경우 재도전)
            boolean alreadySolved = attemptsRepo.existsByMemberIdAndQuizIdAndIsCorrectTrue(memberId, q.getQuizId());
            if (!alreadySolved) {
                // ✅ 옵션은 id=option_no(1~4)로 내려보냄
                var options = optionsRepo.findByQuizIdOrderByOptionNoAsc(q.getQuizId()).stream()
                        .map(o -> QuizOption.builder()
                                .id((long) o.getOptionNo())   // 1..4
                                .label(o.getOptionText())
                                .build())
                        .toList();

                return Optional.of(QuizItem.builder()
                        .id(q.getQuizId())                  // quizId 그대로
                        .question(q.getQuestion())
                        .options(options)
                        .build());
            }
        }
        return Optional.empty();
    }

    @Transactional
    public QuizAnswerResponse answerQuiz(Long memberId, Long quizId, Long optionId) {
        // optionId는 클라에서 보내는 1~4: 즉 option_no 로 취급
        int optionNo = (optionId == null) ? -1 : optionId.intValue();
        if (optionNo < 1 || optionNo > 4) {
            return QuizAnswerResponse.builder()
                    .isCorrect(false)
                    .reward(BigDecimal.ZERO)
                    .cashAfter(null)
                    .message("Invalid option.")
                    .build();
        }

        var quiz = quizzesRepo.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid quiz id"));

        // 이미 정답으로 해결했으면 재보상/재응시 처리 없이 현재 캐시만 반환
        if (attemptsRepo.existsByMemberIdAndQuizIdAndIsCorrectTrue(memberId, quizId)) {
            return QuizAnswerResponse.builder()
                    .isCorrect(true)
                    .reward(BigDecimal.ZERO)
                    .cashAfter(ledgerRepo.sumBalanceByMemberId(memberId))
                    .message("Already solved correctly.")
                    .build();
        }

        // ✅ (quiz_id, option_no)로 정답 판별 (option_id PK는 사용 안 함)
        boolean correct = optionsRepo.findByQuizIdAndOptionNo(quizId, optionNo)
                .map(QuizOptions::getIsCorrect)
                .orElse(false);

        // 회원×퀴즈 1행 유지: 있으면 UPDATE, 없으면 INSERT (selected_option_id에는 option_no를 저장)
        var attemptOpt = attemptsRepo.findByMemberIdAndQuizId(memberId, quizId);
        QuizAttempts attempt = attemptOpt.orElseGet(QuizAttempts::new);

        if (attempt.getAttemptId() == null) {
            attempt.setMemberId(memberId);
            attempt.setQuizId(quizId);
        }
        attempt.setSelectedOptionId((long) optionNo);      // 🔹 option_no 그대로 저장 (1~4)
        attempt.setIsCorrect(correct);
        attempt.setRewarded(false);
        attempt.setAttemptedAt(LocalDateTime.now());
        attemptsRepo.save(attempt);

        BigDecimal reward = BigDecimal.ZERO;
        BigDecimal cashAfter = ledgerRepo.sumBalanceByMemberId(memberId);

        if (correct) {
            // 첫 정답 보상만 지급: 위에서 이미 '이미 정답'은 걸렀으므로 여기선 최초 정답 케이스
            reward = quiz.getRewardCapital();

            var led = new PortfolioLedger();
            led.setMemberId(memberId); // @Column(name="user_id") 매핑 전제
            led.setDelta(reward);
            led.setReason(PortfolioLedger.TransactionReason.QUIZ_REWARD);
            led.setRefId("QUIZ:" + quizId);
            led.setTs(LocalDateTime.now());
            ledgerRepo.save(led);

            attempt.setRewarded(true);
            attemptsRepo.save(attempt);

            cashAfter = cashAfter.add(reward);
        }

        // 🚫 여기서 step 상태는 건드리지 않음 (별도의 completeStep API에서 처리)
        return QuizAnswerResponse.builder()
                .isCorrect(correct)
                .reward(reward)
                .cashAfter(correct ? cashAfter : null)
                .message(correct ? "Correct!" : "Wrong answer. Try again.")
                .build();
    }
}