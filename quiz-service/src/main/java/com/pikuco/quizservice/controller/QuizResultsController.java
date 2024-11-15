package com.pikuco.quizservice.controller;

import com.pikuco.quizservice.dto.QuizResultDto;
import com.pikuco.quizservice.dto.QuizResultsDto;
import com.pikuco.quizservice.entity.SortQuizResultsType;
import com.pikuco.quizservice.mapper.QuizResultsMapper;
import com.pikuco.quizservice.service.QuizResultsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/quiz-results")
//@CrossOrigin
@AllArgsConstructor
public class QuizResultsController {
    private QuizResultsService quizResultsService;

    @GetMapping("/{pseudoId}")
    public ResponseEntity<QuizResultsDto> showQuizResultsByQuizId(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                                                  @PathVariable int pseudoId,
                                                                  @RequestParam(defaultValue = "PLACE_ASC", name = "sort", required = false) String sort,
                                                                  @RequestParam(defaultValue = "uk", name = "lang", required = false) String lang,
                                                                  @RequestParam(required = false, defaultValue = "1", name="page") int pageNo,
                                                                  @RequestParam(required = false, defaultValue = "4", name="pageSize") int pageSize) {
        SortQuizResultsType sortType = SortQuizResultsType.checkType(sort) != null ? SortQuizResultsType.checkType(sort) : SortQuizResultsType.SCORE_DESC;
        QuizResultsDto quizResults = quizResultsService.getQuizResultsById(authHeader, pseudoId, sortType, lang, pageNo, pageSize);
        return ResponseEntity.ok(quizResults);
    }

    @GetMapping("/{pseudoId}/user")
    public ResponseEntity<QuizResultsDto> showIndividualQuizResults(@RequestHeader(name = "Authorization") String authHeader,
                                                                    @PathVariable int pseudoId,
                                                                    @RequestParam(defaultValue = "PLACE_ASC", name = "sort", required = false) String sort,
                                                                    @RequestParam(defaultValue = "uk", name = "lang", required = false) String lang,
                                                                    @RequestParam(required = false, defaultValue = "1", name="page") int pageNo,
                                                                    @RequestParam(required = false, defaultValue = "4", name="pageSize") int pageSize) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
        SortQuizResultsType sortType = SortQuizResultsType.checkType(sort) != null ? SortQuizResultsType.checkType(sort) : SortQuizResultsType.PLACE_ASC;
        QuizResultsDto quizResults = quizResultsService.getIndividualQuizResults(authHeader, pseudoId, sortType, lang, pageNo, pageSize);
        return ResponseEntity.ok(quizResults);
    }

    // Realizes 3 scenarios:
    // 1 - create new quiz results if quiz results with this quiz id hasn't existed yet
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
