package com.pikuco.quizservice.repository;

import com.pikuco.quizservice.entity.Quiz;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, ObjectId> {
    Optional<Quiz> findQuizByPseudoId(int quizId);
    Optional<Quiz> findFirstByOrderByPseudoIdDesc();
    List<Quiz> findAllByIdIn(List<ObjectId> ids);

}
