package com.pikuco.quizservice.dto.quiz;

import com.pikuco.quizservice.dto.CreatorDto;
import com.pikuco.quizservice.entity.Question;

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
        String cover,
        String language,
        String[] languages
) {}