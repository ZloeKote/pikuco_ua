package com.pikuco.quizservice.controller;

import com.pikuco.quizservice.dto.QuizResultDto;
import com.pikuco.quizservice.dto.QuizResultsDto;
import com.pikuco.quizservice.entity.SortQuizResultsType;
import com.pikuco.quizservice.exception.ObjectNotFoundException;
import com.pikuco.quizservice.mapper.QuizResultsMapper;
import com.pikuco.quizservice.service.QuizResultsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("api/v1/quiz-results")
@CrossOrigin(origins = "http://localhost:3000")
@AllArgsConstructor
public class QuizResultsController {
    private QuizResultsService quizResultsService;

    @GetMapping("/{pseudoId}")
    public ResponseEntity<QuizResultsDto> showQuizResultsByQuizId(@PathVariable int pseudoId,
                                                                  @RequestParam(defaultValue = "SCORE_DESC", name = "sort", required = false) String sort) {
        SortQuizResultsType sortType = SortQuizResultsType.checkType(sort) != null ? SortQuizResultsType.checkType(sort) : SortQuizResultsType.SCORE_DESC;
        QuizResultsDto quizResults = QuizResultsMapper.mapToQuizResultsDto(quizResultsService.getQuizResultsById(pseudoId, sortType));
        return ResponseEntity.ok(quizResults);
    }

    @GetMapping("/{pseudoId}/user")
    public ResponseEntity<QuizResultsDto> showIndividualQuizResults(@RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
                                                                    @PathVariable int pseudoId,
                                                                    @RequestParam(defaultValue = "PLACE_ASC", name = "sort", required = false) String sort) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();

        SortQuizResultsType sortType = SortQuizResultsType.checkType(sort) != null ? SortQuizResultsType.checkType(sort) : SortQuizResultsType.PLACE_ASC;
        QuizResultsDto quizResults = QuizResultsMapper.mapToQuizResultsDto(quizResultsService.getIndividualQuizResults(authHeader, pseudoId, sortType));
        return ResponseEntity.ok(quizResults);
    }

    // Realizes 3 scenarios:
    // 1 - create new quiz results if quiz results with this quiz id doesn't exist yes
    // 2 - add to existing quiz results new results
    // 3 - update existing quiz results if participant already has results for this quiz
    @PostMapping("/{pseudoId}")
    public ResponseEntity<?> addQuizResult(@RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
                                           @PathVariable int pseudoId,
                                           @RequestBody QuizResultDto quizResultDto) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();

        quizResultsService.addNewQuizResult(authHeader, QuizResultsMapper.mapToQuizResult(quizResultDto), pseudoId);
        return ResponseEntity.ok().build();
    }
}
