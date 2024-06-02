package com.pikuco.quizservice.mapper;

import com.pikuco.quizservice.dto.quiz.QuizBasicDto;
import com.pikuco.quizservice.dto.quiz.QuizCardDto;
import com.pikuco.quizservice.dto.quiz.QuizDto;
import com.pikuco.quizservice.dto.quiz.QuizTranslationDto;
import com.pikuco.quizservice.entity.Quiz;
import com.pikuco.quizservice.entity.QuizTranslation;
import com.pikuco.quizservice.entity.Type;

public class QuizMapper {
    public static QuizDto mapToQuizDto(Quiz quiz) {
        return new QuizDto(
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getType().getValue(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt(),
                CreatorMapper.mapToCreatorDto(quiz.getCreator()),
                quiz.getQuestions(),
                quiz.getNumQuestions(),
                quiz.getPseudoId(),
                quiz.isRoughDraft(),
                quiz.getCover(),
                quiz.getLanguage(),
                quiz.getLanguages());
    }

    public static QuizBasicDto mapToQuizBasicDto(Quiz quiz) {
        return new QuizBasicDto(quiz.getTitle(),
                quiz.getDescription(),
                quiz.getType().getValue(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt(),
                quiz.getCreator(),
                quiz.getNumQuestions(),
                quiz.getPseudoId(),
                quiz.getCover(),
                quiz.isRoughDraft(),
                quiz.getLanguage(),
                quiz.getLanguages(),
                quiz.getQuestions().size());
    }

    public static QuizCardDto mapToQuizCardDto(Quiz quiz) {
        return new QuizCardDto(quiz.getTitle(),
                quiz.getDescription(),
                quiz.getType().getName(),
                CreatorMapper.mapToCreatorDto(quiz.getCreator()),
                quiz.getPseudoId(),
                quiz.getLanguage(),
                quiz.getLanguages(),
                quiz.isRoughDraft(),
                quiz.getCover());
    }

    public static Quiz mapToQuiz(QuizDto quizDto) {
        Quiz quiz = Quiz.builder()
                .title(quizDto.title())
                .description(quizDto.description())
                .type(Type.valueOf(quizDto.type()))
                .createdAt(quizDto.createdAt())
                .updatedAt(quizDto.updatedAt())
                .questions(quizDto.questions())
                .numQuestions(quizDto.numQuestions())
                .cover(quizDto.cover())
                .pseudoId(quizDto.pseudoId())
                .isRoughDraft(quizDto.isRoughDraft())
                .language(quizDto.language())
                .build();
        if (quizDto.creator() != null) quiz.setCreator(CreatorMapper.mapToCreator(quizDto.creator()));

        return quiz;
    }

    public static QuizTranslation mapToQuizTranslation(QuizTranslationDto quizTranslationDto) {
        return new QuizTranslation(quizTranslationDto.title(),
                quizTranslationDto.description(),
                quizTranslationDto.language(),
                quizTranslationDto.questions());
    }
}
