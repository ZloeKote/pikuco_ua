package com.pikuco.evaluationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDto {
    private int evaluation;
    private boolean isLiked;
    private boolean isDisliked;
}