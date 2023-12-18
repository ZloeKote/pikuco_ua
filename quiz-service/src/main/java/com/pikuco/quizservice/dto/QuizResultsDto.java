package com.pikuco.quizservice.dto;

import com.pikuco.quizservice.entity.QuizResult;
import lombok.Builder;

import java.util.List;

@Builder
public record QuizResultsDto(
        QuizResultDto quizResults
) {

}
