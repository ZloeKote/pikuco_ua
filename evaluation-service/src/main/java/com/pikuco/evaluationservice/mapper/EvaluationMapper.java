package com.pikuco.evaluationservice.mapper;

import com.pikuco.evaluationservice.dto.EvaluationDto;
import com.pikuco.evaluationservice.entity.Evaluation;

public class EvaluationMapper {
    public static EvaluationDto mapToEvaluationDto(Evaluation evaluation) {
        return EvaluationDto.builder()
                .isLiked(evaluation.isLiked())
                .build();
    }

    public static Evaluation mapToEvaluation(EvaluationDto evaluationDto) {
        return Evaluation.builder()
                .isLiked(evaluationDto.isLiked())
                .build();
    }
}
