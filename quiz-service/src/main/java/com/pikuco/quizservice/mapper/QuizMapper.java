package com.pikuco.quizservice.mapper;

import com.pikuco.quizservice.dto.quiz.*;
import com.pikuco.quizservice.entity.Quiz;
import com.pikuco.quizservice.entity.QuizTranslation;
import com.pikuco.quizservice.entity.Type;
import com.pikuco.quizservice.service.QuizService;

import java.util.ArrayList;
import java.util.List;

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
                quiz.getLanguage(),
                QuizService.getLanguages(quiz),
                quiz.getTranslations());
    }

    public static QuizBasicDto mapToQuizBasicDto(Quiz quiz) {
        QuizBasicDto quizBasic = null;
        if (quiz.getTranslations() != null) {
            List<QuizTranslationBasicDto> quizTranslationBasicDtoList = new ArrayList<>();
            for (QuizTranslation quizTranslation : quiz.getTranslations()) {
                quizTranslationBasicDtoList.add(new QuizTranslationBasicDto(quizTranslation.getTitle(),
                        quizTranslation.getDescription(),
                        quizTranslation.getLanguage()));
                quizBasic = new QuizBasicDto(quiz.getTitle(),
                        quiz.getDescription(),
                        quiz.getType().getValue(),
                        quiz.getCreatedAt(),
                        quiz.getUpdatedAt(),
                        quiz.getCreator(),
                        quiz.getNumQuestions(),
                        quiz.getPseudoId(),
                        quiz.isRoughDraft(),
                        quiz.getLanguage(),
                        QuizService.getLanguages(quiz),
                        quiz.getQuestions().size(),
                        quizTranslationBasicDtoList);
            }
        } else {
            quizBasic = new QuizBasicDto(quiz.getTitle(),
                    quiz.getDescription(),
                    quiz.getType().getValue(),
                    quiz.getCreatedAt(),
                    quiz.getUpdatedAt(),
                    quiz.getCreator(),
                    quiz.getNumQuestions(),
                    quiz.getPseudoId(),
                    quiz.isRoughDraft(),
                    quiz.getLanguage(),
                    QuizService.getLanguages(quiz),
                    quiz.getQuestions().size(),
                    new ArrayList<>());
        }
        return quizBasic;
    }

    public static QuizCardDto mapToQuizCardDto(Quiz quiz) {
        return new QuizCardDto(quiz.getTitle(),
                quiz.getDescription(),
                quiz.getType().getName(),
                CreatorMapper.mapToCreatorDto(quiz.getCreator()),
                quiz.getPseudoId(),
                quiz.getLanguage(),
                QuizService.getLanguages(quiz),
                quiz.isRoughDraft());
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
                .pseudoId(quizDto.pseudoId())
                .isRoughDraft(quizDto.isRoughDraft())
                .language(quizDto.language())
                .translations(quizDto.translations())
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
