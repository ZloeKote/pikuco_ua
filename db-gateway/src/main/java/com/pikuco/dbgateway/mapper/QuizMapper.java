package com.pikuco.dbgateway.mapper;

import com.pikuco.dbgateway.entity.Quiz;
import com.pikuco.sharedComps.quizService.dto.QuizDto;

public class QuizMapper {
    public static QuizDto mapToQuizDto(Quiz quiz) {
        return new QuizDto(
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getType(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt(),
                quiz.getCreator(),
                quiz.getQuestions(),
                quiz.getQuizId(),
                quiz.isRoughDraft()
        );
    }

    public static Quiz mapToQuiz(QuizDto quizDto) {
        return Quiz.builder()
                .title(quizDto.title())
                .description(quizDto.description())
                .type(quizDto.type())
                .createdAt(quizDto.createdAt())
                .updatedAt(quizDto.updatedAt())
                .creator(quizDto.creator())
                .questions(quizDto.questions())
                .quizId(quizDto.quizId())
                .isRoughDraft(quizDto.isRoughDraft())
                .build();
    }
}
