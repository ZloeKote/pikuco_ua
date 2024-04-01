package com.pikuco.quizservice.dto;

import com.pikuco.quizservice.entity.Question;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public record QuizTranslationDto(
        String title,
        String description,
        String language,
        List<Question>questions
) {
}
