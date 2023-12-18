package com.pikuco.evaluationservice.service;

import com.pikuco.evaluationservice.entity.Evaluation;
import com.pikuco.evaluationservice.repository.EvaluationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EvaluationService {
    private EvaluationRepository evaluationRepository;

    public List<Evaluation> getEvaluations() {
        return evaluationRepository.findAll();
    }
}
