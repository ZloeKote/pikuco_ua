package com.pikuco.recommendationservice.dto;

public record RecommendationDto(
        QuizBasicDto quiz,
        double score
) {}


