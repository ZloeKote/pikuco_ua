package com.pikuco.sharedComps.quizService.dto;

import com.pikuco.sharedComps.quizService.entity.QuizResult;
import lombok.Builder;

import java.util.List;

@Builder
public record QuizResultsDto(
        List<QuizResult> quizResults
) {

}
