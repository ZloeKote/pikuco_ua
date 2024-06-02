package com.pikuco.quizservice.dto.quiz;

import com.pikuco.quizservice.entity.Creator;

import java.time.LocalDateTime;

public record QuizBasicDto(
        String title,
        String description,
        String type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Creator creator,
        int numQuestions,
        int pseudoId,
        String cover,
        boolean isRoughDraft,
        String language,
        String[] languages,
        int amountQuestions
) {
}
