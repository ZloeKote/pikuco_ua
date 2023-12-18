package com.pikuco.quizservice.controller;

import com.pikuco.quizservice.dto.QuizDto;
import com.pikuco.quizservice.entity.SortQuizResultsType;
import com.pikuco.quizservice.entity.SortType;
import com.pikuco.quizservice.exception.ObjectNotFoundException;
import com.pikuco.quizservice.mapper.QuizMapper;
import com.pikuco.quizservice.service.QuizService;
import com.pikuco.quizservice.utils.Const;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    public ResponseEntity<List<QuizDto>> showQuizzes(@RequestParam(defaultValue = "1", name = "page", required = false) int pageNo) {
        List<QuizDto> quizzes = quizService.getQuizzes(pageNo, Const.PAGE_SIZE)
                .stream().map(QuizMapper::mapToQuizDto).toList();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuizDto>> filterSortQuizzes(@RequestParam(name = "title", required = false) String title,
                                                           @RequestParam(name = "type", required = false) String type,
                                                           @RequestParam(defaultValue = "0", name = "numberQuestions", required = false) int numberQuestions,
                                                           @RequestParam(defaultValue = "0", name = "creatorId", required = false) int creatorId,
                                                           @RequestParam(defaultValue = "NEWEST", name = "sort", required = false) String sort,
                                                           @RequestParam(defaultValue = "1", name = "page", required = false) int pageNo) {
        SortType sortType = SortType.checkType(sort) != null ? SortType.checkType(sort) : SortType.NEWEST;
        List<QuizDto> quizzes = quizService.getFilterSortQuizzes(title, type, numberQuestions, creatorId, sortType, pageNo, Const.PAGE_SIZE)
                .stream().map(QuizMapper::mapToQuizDto).toList();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizDto>> getQuizzesByParticipantId(@PathVariable int userId,
                                                                   @RequestParam(defaultValue = "NEWEST", name = "sort", required = false) String sort,
                                                                   @RequestParam(defaultValue = "1", name = "page", required = false) int pageNo) {
        SortQuizResultsType sortType = SortQuizResultsType.checkType(sort) != null ? SortQuizResultsType.checkType(sort) : SortQuizResultsType.NEWEST;
        List<QuizDto> quizzes = quizService.getQuizzesByParticipantId(userId, sortType, pageNo, Const.PAGE_SIZE)
                .stream().map(QuizMapper::mapToQuizDto).toList();
        return ResponseEntity.ok(quizzes);
    }

    @PostMapping
    public ResponseEntity<Integer> addQuiz(@RequestBody QuizDto quizDto) {
        int id = quizService.addQuiz(QuizMapper.mapToQuiz(quizDto));
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{pseudoId}")
    public ResponseEntity<?> updateQuiz(@PathVariable int pseudoId,
                                        @RequestBody QuizDto quizDto) {
        if (pseudoId != quizDto.pseudoId())
            return ResponseEntity.badRequest().body("Ідентифікатори вікторини не співпадають. Спробуйте знову!");
        quizService.changeQuiz(QuizMapper.mapToQuiz(quizDto));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{pseudoId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable int pseudoId,
                                        @RequestParam("userId") String userId) {
        if (userId == null || userId.isBlank())
            return ResponseEntity.badRequest().body("Ви не авторизовані. Увійдіть в систему та спробуйте ще раз");
        try {

            quizService.deleteQuiz(pseudoId, Integer.parseInt(userId));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Сталася помилка при видаленні вікторини. Спробуйте ще раз");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pseudoId}")
    public ResponseEntity<QuizDto> showQuizByPseudoId(@PathVariable int pseudoId) {
        QuizDto quizDto = QuizMapper.mapToQuizDto(quizService.getQuizByPseudoId(pseudoId));
        return ResponseEntity.ok(quizDto);
    }

//    @GetMapping("/{pseudoId}/user")
//    public ResponseEntity<List<QuizDto>> showQuizzesByCreatorId(@RequestParam("userId") String userId) {
//        if (userId == null || userId.isBlank())
//            throw new ObjectNotFoundException(Collections.singleton("Неправильно вказано id творця"));
//        List<QuizDto> quizzes = quizService.getQuizzesByCreatorId(pseudoId, Integer.parseInt(userId))
//                .stream().map(QuizMapper::mapToQuizDto).toList();
//        return ResponseEntity.ok(quizzes);
//    }
}
