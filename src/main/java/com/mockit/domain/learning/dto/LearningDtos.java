// com.mockit.domain.learning.dto.LearningDtos.java
package com.mockit.domain.learning.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

public class LearningDtos {

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class StepItem {
        private Long id;
        private String title;
        private String status; // DONE / ONGOING / LOCKED
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class StepsResponse {
        private List<StepItem> steps;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class CompleteResponse {
        private boolean success;
        private String message;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class QuizOption {
        private Long id;
        private String label;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class QuizItem {
        private Long id;
        private String question;
        private List<QuizOption> options;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QuizAnswerRequest {
        @JsonProperty("optionId")
        private Long optionId;      // 1..4 (option_no)
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class QuizAnswerResponse {
        private boolean isCorrect;
        private BigDecimal reward;    // 정답일 때만 > 0
        private BigDecimal cashAfter; // 정답일 때만 세팅
        private String message;       // 모두 푼 경우/재응시 등
    }

}