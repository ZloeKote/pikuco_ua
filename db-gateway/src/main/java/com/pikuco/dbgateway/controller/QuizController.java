package com.pikuco.dbgateway.controller;

import com.pikuco.dbgateway.mapper.QuizMapper;
import com.pikuco.dbgateway.service.QuizService;
import com.pikuco.sharedComps.quizService.dto.QuizDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/db/quizzes")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Tournament", description = "The Tournament API")
@AllArgsConstructor
public class QuizController {
    private QuizService quizService;

    @Operation(summary = "Get all battle royales", description = "Get all tournaments")
    @ApiResponse(responseCode = "200", description = "Found tournaments")
    @GetMapping
    public ResponseEntity<List<QuizDto>> showQuizzes() {
        List<QuizDto> tournaments = quizService.getQuizzes()
                .stream()
                .map(QuizMapper::mapToQuizDto).toList();
        return ResponseEntity.ok(tournaments);
    }
}
