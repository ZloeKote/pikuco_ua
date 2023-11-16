package com.pikuco.quizservice.controller;

import com.pikuco.quizservice.service.QuizService;
import com.pikuco.sharedComps.quizService.dto.QuizDto;
import com.pikuco.sharedComps.quizService.dto.QuizResultsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/quizzes")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Tournament", description = "The Tournament API")
@AllArgsConstructor
public class QuizController {
    private QuizService quizService;

    @Operation(summary = "Get all battle royales", description = "Get all tournaments")
    @ApiResponse(responseCode = "200", description = "Found tournaments")
    @GetMapping()
    public ResponseEntity<List<QuizDto>> showQuizzes() {
        List<QuizDto> tournaments = quizService.getQuizzes();
        return ResponseEntity.ok(tournaments);
    }

    @PostMapping
    public ResponseEntity<Integer> addQuiz(@RequestBody QuizDto quizDto) {
        int id = quizService.addQuiz(quizDto);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDto> showQuizById(@PathVariable int quizId) {
        QuizDto quizDto = quizService.getQuizById(quizId);
        return ResponseEntity.ok(quizDto);
    }

    @GetMapping("{quizId}/results")
    public ResponseEntity<QuizResultsDto> showQuizResultsByQuizId(@PathVariable int quizId) {
        QuizResultsDto quizResults = quizService.getQuizResultsByQuizId(quizId);
        return ResponseEntity.ok(quizResults);
    }
}
