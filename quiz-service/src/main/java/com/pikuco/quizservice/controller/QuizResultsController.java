package com.pikuco.quizservice.controller;

import com.pikuco.quizservice.dto.QuizResultDto;
import com.pikuco.quizservice.dto.QuizResultsDto;
import com.pikuco.quizservice.entity.SortQuizResultsType;
import com.pikuco.quizservice.exception.ObjectNotFoundException;
import com.pikuco.quizservice.mapper.QuizResultsMapper;
import com.pikuco.quizservice.service.QuizResultsService;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<QuizResultsDto> showIndividualQuizResults(@PathVariable int pseudoId,
                                                                    @RequestParam("userId") String userId,
                                                                    @RequestParam(defaultValue = "PLACE_ASC", name = "sort", required = false) String sort) {
        if (userId == null || userId.isEmpty()) {
            throw new ObjectNotFoundException(Collections.singleton("Неможливо отримати результати, оскільки не вказано користувача"));
        }
        SortQuizResultsType sortType = SortQuizResultsType.checkType(sort) != null ? SortQuizResultsType.checkType(sort) : SortQuizResultsType.PLACE_ASC;
        QuizResultsDto quizResults = QuizResultsMapper.mapToQuizResultsDto(quizResultsService.getIndividualQuizResults(pseudoId, Integer.parseInt(userId), sortType));
        return ResponseEntity.ok(quizResults);
    }

    @DeleteMapping("/{pseudoId}")
    public ResponseEntity<?> deleteQuizResults(@PathVariable int pseudoId) {
        quizResultsService.deleteQuizResultsByQuizId(pseudoId);
        return ResponseEntity.ok().build();
    }

    // Realizes 3 scenarios:
    // 1 - create new quiz results if quiz results with this quiz id doesn't exist yes
    // 2 - add to existing quiz results new results
    // 3 - update existing quiz results if participant already has results for this quiz
    @PostMapping("/{pseudoId}")
    public ResponseEntity<?> addQuizResult(@PathVariable int pseudoId,
                                           @RequestBody QuizResultDto quizResultDto) {
        quizResultsService.addNewQuizResult(QuizResultsMapper.mapToQuizResult(quizResultDto), pseudoId);
        return ResponseEntity.ok().build();
    }
}
