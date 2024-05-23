package com.pikuco.evaluationservice.service;

//import com.pikuco.sharedComps.evaluationService.dto.EvaluationDto;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(url = "http://localhost:9000", value = "DB-GATEWAY")
public interface DBAPIClient {
    //@GetMapping("/api/v1/db/evaluations")
    //ResponseEntity<List<EvaluationDto>> showEvaluations();
}
