package com.pikuco.dbgateway.repository;

import com.pikuco.dbgateway.entity.Quiz;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, ObjectId> {
    Optional<Quiz> findQuizByQuizId(int quizId);
    Optional<Quiz> findFirstByOrderByQuizIdDesc();
}
