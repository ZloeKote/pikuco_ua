package com.pikuco.recommendationservice.api;

import com.pikuco.recommendationservice.dto.EvaluationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(url = "http://localhost:9091", value = "EVALUATION-SERVICE")
public interface EvaluationAPIClient {
    
    @GetMapping("api/v1/evaluations/user/{userId}")
    ResponseEntity<List<EvaluationDto>> getUserEvaluations(@PathVariable Long userId);
    
    @GetMapping("api/v1/evaluations/quiz/{pseudoId}/count")
    ResponseEntity<Long> getQuizEvaluationCount(
            @PathVariable int pseudoId,
            @RequestParam boolean isLiked);
}


