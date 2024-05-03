package com.pikuco.quizservice.mapper;

import com.pikuco.quizservice.dto.quiz.QuizBasicDto;
import com.pikuco.quizservice.dto.quiz.QuizDto;
import com.pikuco.quizservice.dto.quiz.QuizTranslationBasicDto;
import com.pikuco.quizservice.entity.Quiz;
import com.pikuco.quizservice.entity.QuizTranslation;
import com.pikuco.quizservice.entity.Type;

import java.util.ArrayList;
import java.util.List;

public class QuizMapper {
    public static QuizDto mapToQuizDto(Quiz quiz) {
        return new QuizDto(
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getType().getName(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt(),
                CreatorMapper.mapToCreatorDto(quiz.getCreator()),
                quiz.getQuestions(),
                quiz.getPseudoId(),
                quiz.isRoughDraft(),
                quiz.getLanguage(),
                quiz.getTranslations()
        );
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
                        quiz.getType().getName(),
                        quiz.getCreatedAt(),
                        quiz.getUpdatedAt(),
                        quiz.getCreator(),
                        quiz.getPseudoId(),
                        quiz.isRoughDraft(),
                        quiz.getLanguage(),
                        quiz.getQuestions().size(),
                        quizTranslationBasicDtoList);
            }
        } else {
            quizBasic = new QuizBasicDto(quiz.getTitle(),
                    quiz.getDescription(),
                    quiz.getType().getName(),
                    quiz.getCreatedAt(),
                    quiz.getUpdatedAt(),
                    quiz.getCreator(),
                    quiz.getPseudoId(),
                    quiz.isRoughDraft(),
                    quiz.getLanguage(),
                    quiz.getQuestions().size(),
                    new ArrayList<>());
        }
        return quizBasic;
    }

    public static Quiz mapToQuiz(QuizDto quizDto) {
        return Quiz.builder()
                .title(quizDto.title())
                .description(quizDto.description())
                .type(Type.valueOf(quizDto.type()))
                .createdAt(quizDto.createdAt())
                .updatedAt(quizDto.updatedAt())
                .creator(CreatorMapper.mapToCreator(quizDto.creator()))
                .questions(quizDto.questions())
                .pseudoId(quizDto.pseudoId())
                .isRoughDraft(quizDto.isRoughDraft())
                .language(quizDto.language())
                .translations(quizDto.translations())
                .build();
    }
}
