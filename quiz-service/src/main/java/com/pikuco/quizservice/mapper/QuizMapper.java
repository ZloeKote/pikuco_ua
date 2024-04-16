package com.pikuco.quizservice.mapper;

import com.pikuco.quizservice.dto.quiz.QuizDto;
import com.pikuco.quizservice.entity.Quiz;
import com.pikuco.quizservice.entity.Type;

public class QuizMapper {
    public static QuizDto mapToQuizDto(Quiz quiz) {
        return new QuizDto(
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getType().getName(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt(),
                quiz.getCreator(),
                quiz.getQuestions(),
                quiz.getPseudoId(),
                quiz.isRoughDraft(),
                quiz.getLanguage(),
                quiz.getTranslations()
        );
    }

    public static Quiz mapToQuiz(QuizDto quizDto) {
        return Quiz.builder()
                .title(quizDto.title())
                .description(quizDto.description())
                .type(Type.valueOf(quizDto.type()))
                .createdAt(quizDto.createdAt())
                .updatedAt(quizDto.updatedAt())
                .creator(quizDto.creator())
                .questions(quizDto.questions())
                .pseudoId(quizDto.pseudoId())
                .isRoughDraft(quizDto.isRoughDraft())
                .language(quizDto.language())
                .translations(quizDto.translations())
                .build();
    }
}
