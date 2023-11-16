package com.pikuco.dbgateway.mapper;

import com.pikuco.dbgateway.entity.Quiz;
import com.pikuco.sharedComps.quizService.dto.QuizDto;

public class QuizMapper {
    public static QuizDto mapToQuizDto(Quiz quiz) {
        return new QuizDto(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getType(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt(),
                quiz.getCreator(),
                quiz.getQuestions()
        );
    }

    public static Quiz mapToQuiz(QuizDto quizDto) {
        return new Quiz(
                quizDto.id(),
                quizDto.title(),
                quizDto.description(),
                quizDto.type(),
                quizDto.createdAt(),
                quizDto.updatedAt(),
                quizDto.creator(),
                quizDto.questions()
        );
    }
}
