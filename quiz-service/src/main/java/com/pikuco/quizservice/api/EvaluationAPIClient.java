package com.pikuco.quizservice.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "http://localhost:9091", value = "EVALUATION-SERVICE")
public interface EvaluationAPIClient {
    @DeleteMapping("api/v1/evaluations/quizzes/{quizId}")
    ResponseEntity<?> deleteAllEvaluationsByQuizId(@PathVariable String quizId);
}
