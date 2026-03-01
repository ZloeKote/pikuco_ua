package com.pikuco.recommendationservice.dto;

import java.time.LocalDateTime;

public record QuizResultDto(
        Long participantId,
        LocalDateTime passedAt,
        double score
) {}


