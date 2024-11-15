package com.pikuco.quizservice.dto;

import lombok.Builder;

@Builder
public record QuizResultsDto(
        QuizResultDto quizResults,
        int numPages,
        boolean userResultExists
) {

}
