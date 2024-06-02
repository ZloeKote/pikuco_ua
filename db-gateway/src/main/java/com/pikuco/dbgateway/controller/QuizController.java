package com.pikuco.dbgateway.controller;

import com.pikuco.dbgateway.mapper.QuizMapper;
import com.pikuco.dbgateway.mapper.QuizResultsMapper;
import com.pikuco.dbgateway.service.QuizResultsService;
import com.pikuco.dbgateway.service.QuizService;
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
@RequestMapping("api/v1/db/quizzes")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Tournament", description = "The Tournament API")
@AllArgsConstructor
public class QuizController {
    private QuizService quizService;
    private QuizResultsService quizResultsService;

    @Operation(summary = "Get all battle royales", description = "Get all tournaments")
    @ApiResponse(responseCode = "200", description = "Found tournaments")
    @GetMapping
    public ResponseEntity<List<QuizDto>> showQuizzes() {
        List<QuizDto> tournaments = quizService.getQuizzes()
                .stream()
                .map(QuizMapper::mapToQuizDto).toList();
        return ResponseEntity.ok(tournaments);
    }

    @PostMapping
    public ResponseEntity<Integer> addQuiz(@RequestBody QuizDto quizDto) {
        int id = quizService.addQuiz(QuizMapper.mapToQuiz(quizDto));
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDto> showQuizById(@PathVariable int quizId) {
        QuizDto quizDto = QuizMapper.mapToQuizDto(quizService.getQuizById(quizId));
        return ResponseEntity.ok(quizDto);
    }

    @GetMapping("{quizId}/results")
    public ResponseEntity<QuizResultsDto> showQuizResultsById(@PathVariable int quizId) {
        QuizResultsDto quizResultsDto = QuizResultsMapper.mapToQuizResultsDto(quizResultsService.getQuizResultsById(quizId), 1);
        return ResponseEntity.ok(quizResultsDto);
    }
}
