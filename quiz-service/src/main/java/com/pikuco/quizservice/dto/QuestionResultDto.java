package com.pikuco.quizservice.dto;

import java.util.List;

public record QuestionResultDto (
        String title,
        String description,
        String url,
        int score,
        int place,
        String language
) {
}
