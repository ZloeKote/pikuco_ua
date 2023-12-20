package com.pikuco.quizservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuizResultDto(
        List<QuestionResultDto> questions,
        Long participant_id,
        LocalDateTime passedAt
) {
}
