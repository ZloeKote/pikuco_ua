package com.pikuco.recommendationservice.dto;

import java.time.LocalDateTime;

public record EvaluationDto(
        String id,
        String type,
        Long userId,
        String evaluationObjectId,
        boolean isLiked,
        LocalDateTime evaluatedAt
) {}


