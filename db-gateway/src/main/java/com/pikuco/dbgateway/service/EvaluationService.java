package com.pikuco.dbgateway.service;

import com.pikuco.dbgateway.entity.Evaluation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationService {
    private final MongoTemplate mongoTemplate;

    public EvaluationService(@Qualifier("evaluation-service-template") MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Evaluation> getAllEvaluations() {
        return mongoTemplate.findAll(Evaluation.class, "evaluation");
    }
}
