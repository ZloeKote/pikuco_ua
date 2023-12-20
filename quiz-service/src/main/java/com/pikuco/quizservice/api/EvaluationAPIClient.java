package com.pikuco.quizservice.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "http://localhost:9091", value = "EVALUATION-SERVICE")
public interface EvaluationAPIClient {
    @GetMapping("api/v1/evaluations/quizzes/{quizId}")
    ResponseEntity<Integer> showEvaluationByQuiz(@PathVariable String quizId);
}
