package com.pikuco.quizservice.dto.quiz;

import com.pikuco.quizservice.dto.CreatorDto;
import com.pikuco.quizservice.entity.Question;
import com.pikuco.quizservice.entity.QuizTranslation;

import java.time.LocalDateTime;
import java.util.List;

public record QuizDto(
        String title,
        String description,
        String type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        CreatorDto creator,
        List<Question> questions,
        int numQuestions,
        int pseudoId,
        boolean isRoughDraft,
        String language,
        String[] languages,
        List<QuizTranslation> translations
) {}
