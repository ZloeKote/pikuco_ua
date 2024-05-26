package com.pikuco.evaluationservice.controller;

import com.pikuco.evaluationservice.dto.EvaluationDto;
import com.pikuco.evaluationservice.dto.QuizzesRequest;
import com.pikuco.evaluationservice.dto.QuizzesResponse;
import com.pikuco.evaluationservice.service.EvaluationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/evaluations")
@AllArgsConstructor
public class EvaluationController {
    EvaluationService evaluationService;

    @GetMapping("/quizzes/{pseudoId}")
    public ResponseEntity<EvaluationDto> showEvaluationByPseudoQuizId(@RequestHeader(required = false, value = "Authorization") String authHeader,
                                                                      @PathVariable int pseudoId) {
        EvaluationDto evaluation = evaluationService.getEvaluationByQuizId(authHeader, pseudoId);
        return ResponseEntity.ok(evaluation);
    }

    @PostMapping("/quizzes/{pseudoId}/user")
    public ResponseEntity<?> addEvaluation(@RequestHeader(required = true, value = "Authorization") String authHeader,
                                           @PathVariable int pseudoId,
                                           @RequestBody AddEvaluationRequest isLiked) {
        evaluationService.addEvaluation(authHeader, pseudoId, isLiked.getIsLiked());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/quizzes/{pseudoId}/user")
    public ResponseEntity<?> deleteEvaluation(@RequestHeader(required = true, value = "Authorization") String authHeader,
                                              @PathVariable int pseudoId) {
        evaluationService.deleteEvaluation(authHeader, pseudoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/quizzes/{quizId}")
    public ResponseEntity<?> deleteAllEvaluationsByQuizId(@PathVariable String quizId) {
        evaluationService.deleteQuizEvaluationsByQuizId(quizId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/quizzes/best")
    public ResponseEntity<Map<String, Object>> showBestQuizzesIds(
            @RequestParam List<String> quizzesIds,
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "8") int pageSize) {
        Map<String, Object> quizzesResponse = evaluationService.getBestQuizzesIds(quizzesIds, pageNo, pageSize);
        return ResponseEntity.ok(quizzesResponse);
    }
}
