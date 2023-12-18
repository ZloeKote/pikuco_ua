package com.pikuco.quizservice.service;

//import com.pikuco.sharedComps.quizService.dto.QuizDto;
//import com.pikuco.sharedComps.quizService.dto.QuizResultsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(url = "http://localhost:9000", value = "DB-GATEWAY")
public interface DBAPIClient {
//    @GetMapping("api/v1/db/quizzes")
//    ResponseEntity<List<QuizDto>> showQuizzes();
//
//    @PostMapping("api/v1/db/quizzes")
//    ResponseEntity<Integer> addQuiz(@RequestBody QuizDto quizDto);
//
//    @GetMapping("api/v1/db/quizzes/{pseudoId}")
//    ResponseEntity<QuizDto> showQuizById(@PathVariable int pseudoId);
//
//    @GetMapping("api/v1/db/quizzes/{pseudoId}/results")
//    ResponseEntity<QuizResultsDto> showQuizResultsById(@PathVariable int pseudoId);
}
