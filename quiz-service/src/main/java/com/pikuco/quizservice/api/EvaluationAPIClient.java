package com.pikuco.quizservice.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(url = "http://localhost:9091", value = "EVALUATION-SERVICE")
public interface EvaluationAPIClient {
    @DeleteMapping("api/v1/evaluations/quizzes/{quizId}")
    ResponseEntity<?> deleteAllEvaluationsByQuizId(@PathVariable String quizId);

    @GetMapping("api/v1/evaluations/quizzes/best")
    ResponseEntity<Map<String, Object>> showBestQuizzesIds(
            @RequestParam List<String> quizzesIds,
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "8") int pageSize);
}
