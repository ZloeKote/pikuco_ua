package com.pikuco.recommendationservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuizBasicDto(
        String title,
        String description,
        String type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        CreatorDto creator,
        int numQuestions,
        int pseudoId,
        String cover,
        boolean isRoughDraft,
        String language,
        String[] languages,
        List<String> tags
) {}


