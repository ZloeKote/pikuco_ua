package com.pikuco.quizservice.dto.quiz;

import com.pikuco.quizservice.entity.Creator;

import java.time.LocalDateTime;
import java.util.List;

public record QuizBasicDto(
        String title,
        String description,
        String type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Creator creator,
        int pseudoId,
        boolean isRoughDraft,
        String language,
        String[] languages,
        int amountQuestions,
        List<QuizTranslationBasicDto> translations
) {
}
