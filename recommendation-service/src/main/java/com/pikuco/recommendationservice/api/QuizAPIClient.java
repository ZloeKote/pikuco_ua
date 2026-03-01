package com.pikuco.recommendationservice.api;

import com.pikuco.recommendationservice.dto.QuizBasicDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(url = "http://localhost:9091", value = "QUIZ-SERVICE")
public interface QuizAPIClient {
    
    @GetMapping("api/v1/quizzes/{pseudoId}")
    ResponseEntity<QuizBasicDto> getQuizByPseudoId(@PathVariable int pseudoId);
    
    @GetMapping("api/v1/quizzes/by-id/{quizId}")
    ResponseEntity<QuizBasicDto> getQuizById(@PathVariable String quizId);
    
    @GetMapping("api/v1/quizzes")
    ResponseEntity<List<QuizBasicDto>> showQuizzes(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "8") int pageSize);
    
    @GetMapping("api/v1/quizzes/completed/users/{userId}")
    ResponseEntity<List<Integer>> getCompletedQuizzesPseudoIds(@PathVariable long userId);
    
    @GetMapping("api/v1/quiz-results/quiz/{pseudoId}")
    ResponseEntity<Integer> getQuizCompletionCount(@PathVariable int pseudoId);
}


