package com.pikuco.evaluationservice.mapper;

import com.pikuco.evaluationservice.dto.EvaluationDto;
import com.pikuco.evaluationservice.entity.Evaluation;

public class EvaluationMapper {
    public static EvaluationDto mapToEvaluationDto(Evaluation evaluation) {
        return new EvaluationDto(
                evaluation.getId(),
                evaluation.getType(),
                evaluation.getUserId(),
                evaluation.getEvaluationObjectId(),
                evaluation.isLiked(),
                evaluation.getEvaluatedAt()
        );
    }

    public static Evaluation mapToEvaluation(EvaluationDto evaluationDto) {
        return new Evaluation(
                evaluationDto.id(),
                evaluationDto.type(),
                evaluationDto.userId(),
                evaluationDto.evaluationObjectId(),
                evaluationDto.isLiked(),
                evaluationDto.evaluatedAt()
        );
    }
}
