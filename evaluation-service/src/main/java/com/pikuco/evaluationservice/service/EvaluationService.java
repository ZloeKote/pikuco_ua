package com.pikuco.evaluationservice.service;

import com.pikuco.sharedComps.evaluationService.dto.EvaluationDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EvaluationService {
    private DBAPIClient apiClient;

    public List<EvaluationDto> getEvaluations() {
        return apiClient.showEvaluations().getBody();
    }
}
