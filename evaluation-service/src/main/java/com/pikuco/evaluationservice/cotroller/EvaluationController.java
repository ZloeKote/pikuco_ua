package com.pikuco.evaluationservice.cotroller;

import com.pikuco.evaluationservice.dto.EvaluationDto;
import com.pikuco.evaluationservice.mapper.EvaluationMapper;
import com.pikuco.evaluationservice.service.EvaluationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/evaluations")
@AllArgsConstructor
public class EvaluationController {
    EvaluationService evaluationService;

    @GetMapping
    public ResponseEntity<List<EvaluationDto>> showEvaluations() {
        List<EvaluationDto> evaluations = evaluationService.getEvaluations()
                .stream().map(EvaluationMapper::mapToEvaluationDto).toList();
        return ResponseEntity.ok(evaluations);
    }
}
