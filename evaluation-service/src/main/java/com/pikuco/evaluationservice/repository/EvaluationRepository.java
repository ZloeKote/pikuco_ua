package com.pikuco.evaluationservice.repository;

import com.pikuco.evaluationservice.entity.Evaluation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository extends MongoRepository<Evaluation, ObjectId> {

}
