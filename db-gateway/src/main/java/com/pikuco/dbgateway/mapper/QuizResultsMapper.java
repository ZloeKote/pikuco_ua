package com.pikuco.dbgateway.mapper;

import com.pikuco.dbgateway.entity.QuizResults;
import com.pikuco.sharedComps.quizService.dto.QuizResultsDto;

public class QuizResultsMapper {
    public static QuizResultsDto mapToQuizResultsDto(QuizResults quizResults) {
        return QuizResultsDto.builder()
                .quizResults(quizResults.getResults())
                .build();
    }

    public static QuizResults mapToQuizResults(QuizResultsDto quizResultsDto) {
        return QuizResults.builder()
                .results(quizResultsDto.quizResults())
                .build();
    }


}
