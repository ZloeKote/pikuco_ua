package com.pikuco.quizservice.dto.quiz;

import com.pikuco.quizservice.entity.Creator;

public record QuizCardDto(
        String title,
        String description,
        String type,
        Creator creator,
        int pseudoId,
        String language
) {
}
