package com.pikuco.dbgateway.controller;

import com.pikuco.sharedComps.evaluationService.dto.EvaluationDto;
import com.pikuco.dbgateway.mapper.EvaluationMapper;
import com.pikuco.dbgateway.service.EvaluationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/db/evaluations")
public class EvaluationController {
    EvaluationService evaluationService;

    @GetMapping
    public ResponseEntity<List<EvaluationDto>> showEvaluations() {
        List<EvaluationDto> evaluations = evaluationService.getAllEvaluations()
                .stream()
                .map(EvaluationMapper::mapToEvaluationDto).toList();
        return ResponseEntity.ok(evaluations);
    }
}
