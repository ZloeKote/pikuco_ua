package com.pikuco.quizservice.mapper;

import com.pikuco.quizservice.dto.QuestionResultDto;
import com.pikuco.quizservice.dto.QuizResultDto;
import com.pikuco.quizservice.dto.QuizResultsDto;
import com.pikuco.quizservice.entity.QuestionResult;
import com.pikuco.quizservice.entity.QuizResult;
import com.pikuco.quizservice.entity.QuizResults;

import java.util.ArrayList;
import java.util.List;

public class QuizResultsMapper {
    public static QuizResult mapToQuizResult(QuizResultDto quizResultDto) {
        return QuizResult.builder()
                .questions(quizResultDto.questions().stream().map(QuizResultsMapper::mapToQuestionResult).toList())
                .participant_id(quizResultDto.participant_id())
                .build();
    }

    public static QuestionResult mapToQuestionResult(QuestionResultDto questionResultDto) {
        return QuestionResult.builder()
                .url(questionResultDto.url())
                .place(questionResultDto.place())
                .score(questionResultDto.score())
                .build();
    }
}
