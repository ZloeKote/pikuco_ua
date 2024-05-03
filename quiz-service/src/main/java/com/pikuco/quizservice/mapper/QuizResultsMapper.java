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
    public static QuizResultsDto mapToQuizResultsDto(QuizResults quizResults) {
        return new QuizResultsDto(mapToQuizResultDto(quizResults.getResults().getFirst()));
    }

    public static QuizResults mapToQuizResults(QuizResultsDto quizResultsDto) {
        List<QuizResult> quizResultList = new ArrayList<>();
        quizResultList.add(mapToQuizResult(quizResultsDto.quizResults()));
        return QuizResults.builder()
                .results(quizResultList)
                .build();
    }

    public static QuizResultDto mapToQuizResultDto(QuizResult quizResult) {
        return new QuizResultDto(
                quizResult.getQuestions().stream().map(QuizResultsMapper::mapToQuestionResultDto).toList(),
                quizResult.getParticipant_id(),
                quizResult.getPassedAt());
    }

    public static QuizResult mapToQuizResult(QuizResultDto quizResultDto) {
        return QuizResult.builder()
                .questions(quizResultDto.questions().stream().map(QuizResultsMapper::mapToQuestionResult).toList())
                .participant_id(quizResultDto.participant_id())
                .build();
    }

    public static QuestionResultDto mapToQuestionResultDto(QuestionResult questionResult) {
        return new QuestionResultDto(
                questionResult.getTitle(),
                questionResult.getDescription(),
                questionResult.getUrl(),
                questionResult.getScore(),
                questionResult.getPlace(),
                "",
                new ArrayList<>());
    }

    public static QuestionResult mapToQuestionResult(QuestionResultDto questionResultDto) {
        return QuestionResult.builder()
                .title(questionResultDto.title())
                .description(questionResultDto.description())
                .url(questionResultDto.url())
                .place(questionResultDto.place())
                .score(questionResultDto.score())
                .build();
    }
}
