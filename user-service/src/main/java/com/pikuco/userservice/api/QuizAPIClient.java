package com.pikuco.userservice.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "http://localhost:9091", value = "QUIZ-SERVICE")
public interface QuizAPIClient {
    @PutMapping("api/v1/quizzes/users/{userId}")
    ResponseEntity<?> changeUserNicknameInQuizzes(@PathVariable long userId,
                                                  @RequestBody() String newNickname);
    @DeleteMapping("api/v1/quizzes/users/{userId}")
    ResponseEntity<?> deleteQuizzedByUserId(@PathVariable long userId);
}
