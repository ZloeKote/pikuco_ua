package com.pikuco.quizservice.controller;

import com.pikuco.quizservice.dto.QuizDto;
import com.pikuco.quizservice.dto.QuizListDto;
import com.pikuco.quizservice.entity.Quiz;
import com.pikuco.quizservice.entity.SortQuizResultsType;
import com.pikuco.quizservice.entity.SortType;
import com.pikuco.quizservice.mapper.QuizMapper;
import com.pikuco.quizservice.service.QuizService;
import com.pikuco.quizservice.utils.Const;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/quizzes")
//@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Tournament", description = "The Tournament API")
@AllArgsConstructor
public class QuizController {
    private QuizService quizService;

    @Operation(summary = "Get all battle royales", description = "Get all tournaments")
    @ApiResponse(responseCode = "200", description = "Found tournaments")
    @GetMapping()
    public ResponseEntity<List<QuizDto>> showQuizzes(@RequestParam(defaultValue = "1", name = "page", required = false) int pageNo) {
        List<QuizDto> quizzes = quizService.getQuizzes(pageNo, Const.PAGE_SIZE)
                .stream().map(QuizMapper::mapToQuizDto).toList();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/search")
    public ResponseEntity<QuizListDto> showFilterSortQuizzes(@RequestParam(name = "title", required = false) String title,
                                                           @RequestParam(name = "type", required = false) String type,
                                                           @RequestParam(name = "showRoughDraft", defaultValue = "false", required = false) String showRoughDraft,
                                                           @RequestParam(defaultValue = "0", name = "numberQuestions", required = false) int numberQuestions,
                                                           @RequestParam(defaultValue = "0", name = "creatorId", required = false) int creatorId,
                                                           @RequestParam(defaultValue = "NEWEST", name = "sort", required = false) String sort,
                                                           @RequestParam(defaultValue = "1", name = "page", required = false) int pageNo) {
        SortType sortType = SortType.checkType(sort) != null ? SortType.checkType(sort) : SortType.NEWEST;
        QuizListDto quizzes = quizService.getFilterSortQuizzes(title, type, showRoughDraft, numberQuestions, creatorId, sortType, pageNo, Const.PAGE_SIZE);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/user")
    public ResponseEntity<List<QuizDto>> showQuizzesByParticipantId(@RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
                                                                   @RequestParam(defaultValue = "NEWEST", name = "sort", required = false) String sort,
                                                                   @RequestParam(defaultValue = "1", name = "page", required = false) int pageNo) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();

        SortQuizResultsType sortType = SortQuizResultsType.checkType(sort) != null ? SortQuizResultsType.checkType(sort) : SortQuizResultsType.NEWEST;

        List<QuizDto> quizzes = quizService.getQuizzesByParticipantId(authHeader, sortType, pageNo, Const.PAGE_SIZE)
                .stream().map(QuizMapper::mapToQuizDto).toList();
        return ResponseEntity.ok(quizzes);
    }

    @PostMapping
    public ResponseEntity<Integer> addQuiz(@RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
                                           @RequestBody QuizDto quizDto) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();

        int id = quizService.addQuiz(authHeader, QuizMapper.mapToQuiz(quizDto));
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{pseudoId}")
    public ResponseEntity<?> updateQuiz(@RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
                                        @PathVariable int pseudoId,
                                        @RequestBody QuizDto quizDto) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();
        if (pseudoId != quizDto.pseudoId())
            return ResponseEntity.badRequest().body("Ідентифікатори вікторини не співпадають. Спробуйте знову!");
        quizService.changeQuiz(authHeader, QuizMapper.mapToQuiz(quizDto));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{pseudoId}")
    public ResponseEntity<?> deleteQuiz(@RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
                                        @PathVariable int pseudoId) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body("Ви не авторизовані");
        quizService.deleteQuiz(authHeader, pseudoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pseudoId}")
    public ResponseEntity<QuizDto> showQuizByPseudoId(@PathVariable int pseudoId) {
        QuizDto quizDto = QuizMapper.mapToQuizDto(quizService.getQuizByPseudoId(pseudoId));
        Collections.shuffle(quizDto.questions());
        return ResponseEntity.ok(quizDto);
    }

    @GetMapping("/{pseudoId}/id")
    public ResponseEntity<String> showQuizIdByPseudoId(@PathVariable int pseudoId) {
        Quiz quiz = quizService.getQuizByPseudoId(pseudoId);
        return ResponseEntity.ok(String.valueOf(quiz.getId()));
    }
}
