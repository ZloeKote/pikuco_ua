package com.pikuco.wishlistservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "http://localhost:9091", value = "QUIZ-GATEWAY")
public interface QuizAPIClient {
    @GetMapping("api/v1/quizzes/{pseudoId}/id")
    ResponseEntity<String> showQuizIdByPseudoId(@PathVariable int pseudoId);
}
