package com.pikuco.quizservice.dto;

public record QuestionTranslationDto(
        String title,
        String description,
        String language
) {
}
