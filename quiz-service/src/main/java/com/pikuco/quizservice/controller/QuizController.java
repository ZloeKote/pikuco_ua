package com.pikuco.quizservice.controller;

import com.pikuco.quizservice.dto.quiz.QuizBasicDto;
import com.pikuco.quizservice.dto.quiz.QuizDto;
import com.pikuco.quizservice.dto.quiz.QuizListDto;
import com.pikuco.quizservice.dto.quiz.QuizTranslationDto;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<QuizListDto> showFilterSortQuizzes(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "showRoughDraft", defaultValue = "false", required = false) String showRoughDraft,
            @RequestParam(defaultValue = "0", name = "numberQuestions", required = false) int numberQuestions,
            @RequestParam(name = "creatorNickname", required = false) String creatorNickname,
            @RequestParam(defaultValue = "NEWEST", name = "sort", required = false) String sort,
            @RequestParam(defaultValue = "uk", name = "lang", required = false) String lang,
            @RequestParam(defaultValue = "1", name = "page", required = false) int pageNo,
            @RequestParam(defaultValue = "8", name = "pageSize", required = false) int pageSize) {
        SortType sortType = SortType.checkType(sort) != null ? SortType.checkType(sort) : SortType.NEWEST;
        QuizListDto quizzes = quizService.getFilterSortQuizzes(title, type, showRoughDraft, numberQuestions, creatorNickname, sortType, lang, pageNo, pageSize);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/popular")
    public ResponseEntity<QuizListDto> showPopularQuizzes(
            @RequestParam(defaultValue = "uk", name = "lang", required = false) String lang,
            @RequestParam(defaultValue = "1", name = "page", required = false) int pageNo,
            @RequestParam(defaultValue = "8", name = "pageSize", required = false) int pageSize) {
        QuizListDto quizzes = quizService.getPopularQuizzes(lang, pageNo, pageSize);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/user")
    public ResponseEntity<QuizListDto> showQuizzesByParticipantId(
            @RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
            @RequestParam(defaultValue = "NEWEST", name = "sort", required = false) String sort,
            @RequestParam(defaultValue = "uk", name = "lang", required = false) String lang,
            @RequestParam(defaultValue = "1", name = "page", required = false) int pageNo) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();

        SortQuizResultsType sortType = SortQuizResultsType.checkType(sort) != null ?
                SortQuizResultsType.checkType(sort) : SortQuizResultsType.NEWEST;

        QuizListDto quizzes = quizService
                .getQuizzesByParticipantId(authHeader, sortType, lang, pageNo, Const.PAGE_SIZE_USER_COMPLETED_QUIZZES);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/user/wishlist")
    public ResponseEntity<QuizListDto> showWishlistedQuizzesByUserId(
            @RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
            @RequestParam(defaultValue = "uk", name = "lang", required = false) String lang,
            @RequestParam(defaultValue = "1", name = "page", required = false) int pageNo,
            @RequestParam(defaultValue = "4", name = "pageSize", required = false) int pageSize
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();

        QuizListDto quizzes = quizService.getWishlistedQuizzesByUserId(authHeader, lang, pageNo, pageSize);
        return ResponseEntity.ok(quizzes);
    }

    @PostMapping
    public ResponseEntity<Integer> addQuiz(
            @RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
            @RequestBody QuizDto quizDto) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).build();

        int id = quizService.addQuiz(authHeader, QuizMapper.mapToQuiz(quizDto));
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{pseudoId}")
    public ResponseEntity<?> updateQuiz(
            @RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
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
    public ResponseEntity<?> deleteQuiz(
            @RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
            @PathVariable int pseudoId) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body("Ви не авторизовані");
        quizService.deleteQuiz(authHeader, pseudoId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{pseudoId}/translations")
    public ResponseEntity<?> addQuizTranslation(
            @RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
            @PathVariable int pseudoId,
            @RequestBody QuizTranslationDto quizTranslation) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body("Ви не авторизовані");
        quizService.addQuizTranslation(authHeader, pseudoId, QuizMapper.mapToQuizTranslation(quizTranslation));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{pseudoId}/translations/{language}")
    public ResponseEntity<?> editQuizTranslation(
            @RequestHeader(defaultValue = "none", name = "Authorization") String authHeader,
            @PathVariable int pseudoId,
            @PathVariable String language,
            @RequestBody QuizTranslationDto quizTranslation) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body("Ви не авторизовані");
        quizService
                .editQuizTranslation(authHeader, pseudoId, QuizMapper.mapToQuizTranslation(quizTranslation), language);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{pseudoId}")
    public ResponseEntity<QuizDto> showQuizByPseudoId(@PathVariable int pseudoId) {
        QuizDto quizDto = QuizMapper.mapToQuizDto(quizService.getQuizByPseudoId(pseudoId));
        //Collections.shuffle(quizDto.questions());
        return ResponseEntity.ok(quizDto);
    }

    @GetMapping("/{pseudoId}/main")
    public ResponseEntity<QuizBasicDto> showQuizBasicByPseudoId(@PathVariable int pseudoId) {
        QuizBasicDto quizDto = QuizMapper.mapToQuizBasicDto(quizService.getQuizByPseudoId(pseudoId));
        return ResponseEntity.ok(quizDto);
    }

    @GetMapping("/{pseudoId}/id")
    public ResponseEntity<String> showQuizIdByPseudoId(@PathVariable int pseudoId) {
        Quiz quiz = quizService.getQuizByPseudoId(pseudoId);
        return ResponseEntity.ok(String.valueOf(quiz.getId()));
    }
}
