package com.pikuco.evaluationservice.dto;

import java.time.LocalDateTime;

public record EvaluationDto (
        String id,
        String type,
        int userId,
        String evaluationObjectId,
        boolean isLiked,
        LocalDateTime evaluatedAt
){}
