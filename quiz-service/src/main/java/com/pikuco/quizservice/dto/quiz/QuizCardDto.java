package com.pikuco.quizservice.dto.quiz;

import com.pikuco.quizservice.dto.CreatorDto;

public record QuizCardDto(
        String title,
        String description,
        String type,
        CreatorDto creator,
        int pseudoId,
        String language,
        String[] languages,
        boolean isRoughDraft,
        String cover
) {
}
