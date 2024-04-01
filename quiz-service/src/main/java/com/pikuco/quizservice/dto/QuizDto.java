package com.pikuco.quizservice.dto;


import com.pikuco.quizservice.entity.Creator;
import com.pikuco.quizservice.entity.Question;
import com.pikuco.quizservice.entity.QuizTranslation;

import java.time.LocalDateTime;
import java.util.List;

public record QuizDto (
        String title,
        String description,
        String type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Creator creator,
        List<Question> questions,
        int pseudoId,
        boolean isRoughDraft,
        String language,
        List<QuizTranslation> translations
) {}
