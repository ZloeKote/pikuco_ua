package com.pikuco.quizservice.dto.quiz;

import com.pikuco.quizservice.entity.Question;

import java.util.List;

public record QuizTranslationDto(
        String title,
        String description,
        String language,
        List<Question>questions
) {
}
