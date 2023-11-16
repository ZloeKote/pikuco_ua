package com.pikuco.dbgateway.mapper;


import com.pikuco.dbgateway.entity.Evaluation;
import com.pikuco.sharedComps.evaluationService.dto.EvaluationDto;

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
