package com.pikuco.quizservice.dto;

public record QuestionResultDto (
        String title,
        String description,
        String url,
        int score,
        int place
) {
}
