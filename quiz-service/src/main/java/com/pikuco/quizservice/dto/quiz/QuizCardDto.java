package com.pikuco.quizservice.dto.quiz;

import com.pikuco.quizservice.dto.CreatorDto;

import java.util.List;

public record QuizCardDto(
        String title,
        String description,
        String type,
        CreatorDto creator,
        int pseudoId,
        String language,
        String[] languages,
        boolean isRoughDraft,
        String cover,
        List<String> tags
) {
}
