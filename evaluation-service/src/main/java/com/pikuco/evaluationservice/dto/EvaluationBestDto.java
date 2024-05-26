package com.pikuco.evaluationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationBestDto {
    String quizId;
    int evaluation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvaluationBestDto that = (EvaluationBestDto) o;
        return Objects.equals(quizId, that.quizId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(quizId);
    }
}
